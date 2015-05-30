package transform.services;

import transform.Transform;
import transform.TransformRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformProgressDetailGet extends PluginService {

    private static final String SERVICE_NAME = "transform.progress-detail.get";
    private Interface _defn;

    public SvcTransformProgressDetailGet() {
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
        return "Gets the progress detail.";
    }

    @Override
    public void execute(Element args, Inputs arg1, Outputs arg2, XmlWriter w) throws Throwable {
        long uid = args.longValue("uid");
        Transform t = TransformRepository.getInstance(executor()).get(uid);
        XmlDoc.Element pde = t.progressDetail();
        if (pde != null) {
            w.add(pde, true);
        }
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
