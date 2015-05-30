package transform.services;

import java.util.Map;

import transform.Transform;
import transform.Transform.Status;
import transform.TransformRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.ServiceExecutor;
import arc.mf.plugin.atomic.AtomicOperation;
import arc.mf.plugin.atomic.AtomicTransaction;
import arc.mf.plugin.dtype.LongType;
import arc.mf.plugin.dtype.StringType;
import arc.mf.plugin.dtype.XmlDocType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformExecute extends PluginService {

    public static final String SERVICE_NAME = "transform.execute";

    private Interface _defn;

    public SvcTransformExecute() {
        _defn = new Interface();
        _defn.add(new Interface.Element("uid", new LongType(1, Long.MAX_VALUE), "The unique id of the transform.", 1, 1));

        Interface.Element re = new Interface.Element("runtime", XmlDocType.DEFAULT,
                "The runtime properties for the transform if any. Overwrite if the property exists.", 0, 1);
        Interface.Element rpe = new Interface.Element("property", StringType.DEFAULT,
                "The runtime property for the transform.", 1, Integer.MAX_VALUE);
        rpe.add(new Interface.Attribute("name", StringType.DEFAULT, "The name of the runtime property.", 1));
        re.add(rpe);
        _defn.add(re);

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
        return "Executes a pending transform. Do nothing if the transform is in any other state (running,suspended, or terminated).";
    }

    @Override
    public void execute(final Element args, Inputs in, Outputs out, XmlWriter w) throws Throwable {
        new AtomicTransaction(new AtomicOperation() {
            @Override
            public boolean execute(ServiceExecutor executor) throws Throwable {

                long uid = args.longValue("uid");
                Map<String, String> props = Transform.parseRuntimeProperties(args.element("runtime"));
                Transform transform = TransformRepository.getInstance(executor).get(uid);
                transform.updateStatus();
                if (transform.status().state() == Status.State.TERMINATED) {
                    // it is a re-run.
                    transform.setStatus(Status.State.PENDING);
                }
                if (props != null) {
                    for (String name : props.keySet()) {
                        transform.setRuntimeProperty(name, props.get(name));
                    }
                }
                System.out.println(transform.status().state());
                if (Status.State.PENDING == transform.status().state()) {
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
