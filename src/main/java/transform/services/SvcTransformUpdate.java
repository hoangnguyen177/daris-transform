package transform.services;

import transform.Transform;
import transform.Transform.Log;
import transform.TransformRepository;
import transform.util.ObjectUtil;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.ServiceExecutor;
import arc.mf.plugin.atomic.AtomicOperation;
import arc.mf.plugin.atomic.AtomicTransaction;
import arc.mf.plugin.dtype.EnumType;
import arc.mf.plugin.dtype.IntegerType;
import arc.mf.plugin.dtype.LongType;
import arc.mf.plugin.dtype.StringType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformUpdate extends PluginService {

    private static final String SERVICE_NAME = "transform.update";
    private Interface _defn;

    public SvcTransformUpdate() {
        _defn = new Interface();
        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform.", 1, 1));
        _defn.add(new Interface.Element("name", StringType.DEFAULT, "The name of the transform.", 0, 1));
        _defn.add(new Interface.Element("description", StringType.DEFAULT, "The description for the transform.", 0, 1));

        Interface.Element le = new Interface.Element("log", StringType.DEFAULT, "The log message.", 0, 1);
        le.add(new Interface.Attribute("type", new EnumType(Transform.Log.EventType.stringValues()),
                "The type of the log event.", 1));
        _defn.add(le);

        Interface.Element pe = new Interface.Element("progress", IntegerType.POSITIVE,
                "A positive integer indicates the progress.", 0, 1);
        pe.add(new Interface.Attribute("total", IntegerType.POSITIVE, "A positive number represents the total.", 1));
        _defn.add(pe);

        _defn.add(new Interface.Element("status", new EnumType(Transform.Status.State.stringValues()),
                "The state of the transform.", 0, 1));

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
        return "Updates the transform.";
    }

    @Override
    public void execute(final Element args, Inputs in, Outputs out, XmlWriter w) throws Throwable {

        new AtomicTransaction(new AtomicOperation() {
            @Override
            public boolean execute(ServiceExecutor executor) throws Throwable {
                long uid = args.longValue("uid");
                Transform t = TransformRepository.getInstance(executor).get(uid);
                boolean changed = false;
                String name = args.value("name");
                if (name != null) {
                    t.setName(name);
                    changed = true;
                }
                String description = args.value("description");
                if (description != null) {
                    t.setDescription(description);
                    changed = true;
                }
                if (args.element("log") != null) {
                    Log.EventType eventType = Log.EventType.fromString(args.value("log/@type"));
                    String msg = args.value("log");
                    t.log().addEntry(eventType, msg);
                    changed = true;
                }
                if (args.element("progress") != null) {
                    int progress = args.intValue("progress");
                    int total = args.intValue("progress/@total");
                    if (total < progress) {
                        throw new Exception("The progress number should be less than or equal to the total number.");
                    }
                    t.setProgress(progress, total);
                    changed = true;
                }
                if (args.element("status") != null) {
                    Transform.Status.State state = Transform.Status.State.fromString(args.value("status"));
                    if (!ObjectUtil.equals(state, t.status().state())) {
                        t.setStatus(state);
                        changed = true;
                    }
                }
                if (changed) {
                    t.commitChanges();
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
