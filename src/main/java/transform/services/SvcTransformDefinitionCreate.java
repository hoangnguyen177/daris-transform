package transform.services;

import java.util.List;

import transform.ParameterDefinition;
import transform.ParameterDefinition.DataType;
import transform.TransformDefinition;
import transform.TransformDefinitionRepository;
import transform.TransformProviderRegistry;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.ServiceExecutor;
import arc.mf.plugin.atomic.AtomicOperation;
import arc.mf.plugin.atomic.AtomicTransaction;
import arc.mf.plugin.dtype.EnumType;
import arc.mf.plugin.dtype.IntegerType;
import arc.mf.plugin.dtype.StringType;
import arc.mf.plugin.dtype.XmlDocType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformDefinitionCreate extends PluginService {

    public static final String SERVICE_NAME = "transform.definition.create";

    private Interface _defn;

    public SvcTransformDefinitionCreate() {
        _defn = new Interface();
        _defn.add(new Interface.Element("type", new EnumType(TransformProviderRegistry.providerTypes().toArray(
                new String[0])), "The type of the transform.", 1, 1));
        addToInterface(_defn);
    }

    public static void addToInterface(Interface defn) {
        defn.add(new Interface.Element("name", StringType.DEFAULT, "The name of the transform.", 0, 1));
        defn.add(new Interface.Element("description", StringType.DEFAULT, "The description of the transform.", 0, 1));

        Interface.Element pe = new Interface.Element("parameter", XmlDocType.DEFAULT,
                "A parameter defintion of the transform.", 0, Integer.MAX_VALUE);
        pe.add(new Interface.Attribute("name", StringType.DEFAULT, "The name of the parameter.", 1));
        pe.add(new Interface.Attribute("type", new EnumType(DataType.values()), "The type of the parameter.", 1));
        pe.add(new Interface.Attribute("min-occurs", IntegerType.DEFAULT,
                "The minimum occurs of the parameter. Defaults to 1", 0));
        pe.add(new Interface.Attribute("max-occurs", IntegerType.DEFAULT,
                "The maximum occurs of the parameter. Defaults to 1", 0));
        pe.add(new Interface.Element("description", StringType.DEFAULT, "The description of the paramter.", 0, 1));
        pe.add(new Interface.Element("value", StringType.DEFAULT,
                "The (constant) value of the parameter. Overrides default value if presented.", 0, 1));
        defn.add(pe);
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
        return "Creates a transform definition.";
    }

    @Override
    public int maxNumberOfInputs() {
        return 1;
    }

    @Override
    public int minNumberOfInputs() {
        return 0;
    }

    @Override
    public void execute(final Element args, final Inputs inputs, final Outputs outputs, final XmlWriter w)
            throws Throwable {

        new AtomicTransaction(new AtomicOperation() {
            @Override
            public boolean execute(ServiceExecutor executor) throws Throwable {
                // parse args
                String type = args.value("type");
                String name = args.value("name");
                String description = args.value("description");
                List<ParameterDefinition> paramDefns = ParameterDefinition.instantiateList(args.elements("parameter"));
                PluginService.Input input = inputs == null ? null : inputs.input(0);
                TransformDefinition td = TransformDefinitionRepository.getInstance(executor).create(type, name,
                        description, paramDefns, input);
                w.add("uid", new String[] { "asset", td.assetId() }, td.uid());
                return false;
            }
        }).execute(executor());

    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }
}
