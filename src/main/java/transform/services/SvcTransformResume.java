package transform.services;

import transform.Transform;
import transform.TransformRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformResume extends PluginService {

    private static final String SERVICE_NAME = "transform.resume";
    private Interface _defn;

    public SvcTransformResume() {
        _defn = new Interface();
        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform.", 1, 1));
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
        return "Resumes the specified transform if it is suspended.";
    }

    @Override
    public void execute(Element args, Inputs inputs, Outputs outputs, XmlWriter w) throws Throwable {
        long uid = args.longValue("uid");
        Transform t = TransformRepository.getInstance(executor()).get(uid);
        t.resume();
        t.updateStatus();
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
