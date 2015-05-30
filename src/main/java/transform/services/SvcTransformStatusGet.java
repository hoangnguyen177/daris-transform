package transform.services;

import java.util.List;

import transform.Transform;
import transform.Transform.Status.State;
import transform.TransformRepository;
import transform.util.DateTimeUtil;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.ServiceExecutor;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformStatusGet extends PluginService {

    private static final String SERVICE_NAME = "transform.status.get";
    private Interface _defn;

    public SvcTransformStatusGet() {
        _defn = new Interface();
        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform. If not given",
                0, 1));
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
        return "Returns the status of the transform.";
    }

    @Override
    public void execute(Element args, Inputs in, Outputs out, XmlWriter w) throws Throwable {
        if (args.elementExists("uid")) {
            long uid = args.longValue("uid");
            getStatus(executor(), uid, w);
        } else {
            List<XmlDoc.Element> tes = executor().execute(SvcTransformList.SERVICE_NAME,
                    "<args><size>infinity</size></args>", null, null).elements("transform");
            if (tes != null) {
                for (XmlDoc.Element te : tes) {
                    long uid = te.longValue("@uid");
                    getStatus(executor(), uid, w);
                }
            }
        }
    }

    public static void getStatus(ServiceExecutor executor, long uid, XmlWriter w) throws Throwable {
        Transform transform = TransformRepository.getInstance(executor).get(uid);
        if (transform.status().state() != Transform.Status.State.TERMINATED && transform.status().state() != Transform.Status.State.FAILED) {
            transform.updateStatus();
        }
        State state = transform.status() != null ? transform.status().state() : null;
        w.add("status",
                new String[] { "uid", Long.toString(transform.uid()), "time",
                        DateTimeUtil.formatDateTime(transform.status().time()) }, state);
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
