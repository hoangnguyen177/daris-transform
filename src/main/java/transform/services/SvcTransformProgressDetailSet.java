package transform.services;

import transform.Transform;
import transform.TransformRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.LongType;
import arc.mf.plugin.dtype.XmlDocType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformProgressDetailSet extends PluginService {

    private static final String SERVICE_NAME = "transform.progress-detail.set";
    private Interface _defn;

    public SvcTransformProgressDetailSet() {
        _defn = new Interface();
        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform.", 1, 1));
        Interface.Element pde = new Interface.Element("progress-detail", XmlDocType.DEFAULT, "The progress detail.", 1,
                1);
        pde.setIgnoreDescendants(true);
        _defn.add(pde);
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
        return "Sets the progress detail in XML format.";
    }

    @Override
    public void execute(Element args, Inputs arg1, Outputs arg2, XmlWriter arg3) throws Throwable {
        long uid = args.longValue("uid");
        Transform t = TransformRepository.getInstance(executor()).get(uid);
        t.setProgressDetail(args.element("progress-detail"));
        t.commitChanges();
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
