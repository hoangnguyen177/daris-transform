package transform.services;

import transform.Transform;
import transform.TransformRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformProgressGet extends PluginService {

    private static final String SERVICE_NAME = "transform.progress.get";
    private Interface _defn;

    public SvcTransformProgressGet() {
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
        return "Returns the progress of the transform (if the progress is set).";
    }

    @Override
    public void execute(Element args, Inputs arg1, Outputs arg2, XmlWriter w) throws Throwable {
        long uid = args.longValue("uid");
        Transform t = TransformRepository.getInstance(executor()).get(uid);
        Transform.Progress progress = t.progress();
        if (progress != null) {
            progress.save(w);
        }
    }

    @Override
    public String name() {

        return SERVICE_NAME;
    }

}
