package transform.services;

import transform.Transform;
import transform.TransformRepository;
import transform.Transform.Log;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.EnumType;
import arc.mf.plugin.dtype.LongType;
import arc.mf.plugin.dtype.StringType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformLog extends PluginService {

    private static final String SERVICE_NAME = "transform.log";
    private Interface _defn;

    public SvcTransformLog() {
        _defn = new Interface();

        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform.", 1, 1));

        Interface.Element le = new Interface.Element("log", StringType.DEFAULT, "The log message.", 1, 1);
        le.add(new Interface.Attribute("type", new EnumType(Transform.Log.EventType.stringValues()),
                "The type of the log message.", 1));
        _defn.add(le);
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
        return "Logs a message for the transform.";
    }

    @Override
    public void execute(Element args, Inputs inputs, Outputs outputs, XmlWriter w) throws Throwable {
        Log.EventType type = Log.EventType.fromString(args.value("log/@type"));
        String msg = args.value("log");
        long uid = args.longValue("uid");
        Transform t = TransformRepository.getInstance(executor()).get(uid);
        t.log().addEntry(type, msg);
        t.commitChanges();
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
