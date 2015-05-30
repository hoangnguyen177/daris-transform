package transform.services;

import transform.Transform;
import transform.TransformRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformLogGet extends PluginService {

    private static final String SERVICE_NAME = "transform.log.get";
    private Interface _defn;

    public SvcTransformLogGet() {
        _defn = new Interface();
        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform.", 1, 1));
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
        return "Gets the log entries of the transform.";
    }

    @Override
    public void execute(Element args, Inputs arg1, Outputs arg2, XmlWriter w) throws Throwable {
        Transform t = TransformRepository.getInstance(executor()).get(args.longValue("uid"));
        t.log().save(w);
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
