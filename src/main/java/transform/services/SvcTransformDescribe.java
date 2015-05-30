package transform.services;

import transform.Transform;
import transform.TransformRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.ServiceExecutor;
import arc.mf.plugin.atomic.AtomicOperation;
import arc.mf.plugin.atomic.AtomicTransaction;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformDescribe extends PluginService {

    public static final String SERVICE_NAME = "transform.describe";

    private Interface _defn;

    public SvcTransformDescribe() {
        _defn = new Interface();
        _defn.add(new Interface.Element("uid", new LongType(1, Long.MAX_VALUE), "The unique id of the transform.", 1, 1));
    }

    @Override
    public Access access() {
        return ACCESS_ACCESS;
    }

    @Override
    public Interface definition() {
        return _defn;
    }

    @Override
    public String description() {
        return "Describes a transform.";
    }

    @Override
    public void execute(final Element args, Inputs in, Outputs out, final XmlWriter w) throws Throwable {

        new AtomicTransaction(new AtomicOperation() {
            @Override
            public boolean execute(ServiceExecutor executor) throws Throwable {
                long uid = args.longValue("uid");
                Transform transform = TransformRepository.getInstance(executor).get(uid);
//                if (transform.status().state() != Transform.Status.State.TERMINATED) {
//                    transform.updateStatus();
//                }
                w.push("transform", new String[] { "asset", transform.assetId() });
                transform.save(w, false);
                if (transform.hasContent()) {
                    w.add(transform.contentElement());
                }
                w.pop();
                return false;
            }
        }).execute(executor());

    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
