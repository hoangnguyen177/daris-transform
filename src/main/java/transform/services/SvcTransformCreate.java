package transform.services;

import java.io.File;
import java.util.List;
import java.util.Map;

import transform.Parameter;
import transform.Transform;
import transform.TransformDefinition;
import transform.TransformDefinitionRepository;
import transform.TransformRepository;
import transform.util.StreamUtil;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.ServiceExecutor;
import arc.mf.plugin.atomic.AtomicOperation;
import arc.mf.plugin.atomic.AtomicTransaction;
import arc.mf.plugin.dtype.BooleanType;
import arc.mf.plugin.dtype.IntegerType;
import arc.mf.plugin.dtype.LongType;
import arc.mf.plugin.dtype.StringType;
import arc.mf.plugin.dtype.XmlDocType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformCreate extends PluginService {

    public static final String SERVICE_NAME = "transform.create";

    private Interface _defn;

    public SvcTransformCreate() {
        _defn = new Interface();
        Interface.Element uid = new Interface.Element("definition", LongType.POSITIVE_ONE,
                "The unique id of the transform definition.", 1, 1);
        uid.add(new Interface.Attribute(
                "version",
                new IntegerType(0, Integer.MAX_VALUE),
                "The version of the transform definition. A value of zero means the latest version. Defaults to latest.",
                0));
        _defn.add(uid);
        _defn.add(new Interface.Element("execute", BooleanType.DEFAULT, "Execute after creation. Defaults to true.", 0,
                1));
        addToInterface(_defn);
    }

    public static void addToInterface(Interface defn) {
        defn.add(new Interface.Element("name", StringType.DEFAULT, "The name of the transform.", 0, 1));
        defn.add(new Interface.Element("description", StringType.DEFAULT, "The description for the transform.", 0, 1));

        Interface.Element pe = new Interface.Element(
                "parameter",
                StringType.DEFAULT,
                "The parameter value for the transform. It must be one of the paramters defined in the transform definition.",
                0, Integer.MAX_VALUE);
        pe.add(new Interface.Attribute("name", StringType.DEFAULT, "the name of the parameter.", 1));
        defn.add(pe);

        Interface.Element re = new Interface.Element("runtime", XmlDocType.DEFAULT,
                "The runtime properties for the transform if any.", 0, 1);
        Interface.Element rpe = new Interface.Element("property", StringType.DEFAULT,
                "The runtime property for the transform.", 1, Integer.MAX_VALUE);
        rpe.add(new Interface.Attribute("name", StringType.DEFAULT, "The name of the runtime property.", 1));
        re.add(rpe);
        defn.add(re);

    }

    @Override
    public Access access() {
        return ACCESS_MODIFY;
    }

    @Override
    public Interface definition() {
        return _defn;
    }

    @Override
    public String description() {
        return "Create transform (instance) (and execute it).";
    }

    @Override
    public void execute(final Element args, Inputs inputs, Outputs outputs, final XmlWriter w) throws Throwable {

        new AtomicTransaction(new AtomicOperation() {
            @Override
            public boolean execute(ServiceExecutor executor) throws Throwable {
                long defnUid = args.longValue("definition");
                int defnVersion = args.intValue("definition/@version", 0);
                TransformDefinition defn = TransformDefinitionRepository.getInstance(executor)
                        .get(defnUid, defnVersion);

                String name = args.value("name");
                String description = args.value("description");
                boolean execute = args.booleanValue("execute", true);
                List<Parameter> params = Parameter.instantiateList(args.elements("parameter"));
                // validate parameters
                defn.valiateParameters(params);

                Map<String, String> properties = Transform.parseRuntimeProperties(args.element("runtime"));

                PluginService.Input contentInput = null;
                PluginService.Output defnContentOutput = defn.contentOutput();
                if (defnContentOutput != null) {
                    File tf = PluginService.createTemporaryFile();
                    StreamUtil.save(defn.contentOutput().stream(), tf);
                    contentInput = new PluginService.Input(PluginService.deleteOnCloseInputStream(tf), tf.length(),
                            defn.contentElement().value("type"), null);
                }
                Transform transform = TransformRepository.getInstance(executor).create(defnUid, defnVersion, name,
                        description, params, properties, contentInput);
                w.add("uid", new String[] { "asset", transform.assetId() }, transform.uid());

                // execute the transform
                if (execute) {
                    transform.execute();
                    Thread.sleep(100);
                    transform.updateStatus();
                }

                return false;
            }
        }).execute(executor());
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
