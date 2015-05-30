package transform.services;

import java.util.List;

import transform.Transform;
import transform.TransformRepository;
import transform.mf.AssetUtils;
import arc.mf.plugin.PluginService;
import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformOutputRemove extends PluginService {

    private static final String SERVICE_NAME = "transform.output.remove";
    private Interface _defn;

    public SvcTransformOutputRemove() {
        _defn = new Interface();
        SvcTransformOutputAdd.addToInterface(_defn);
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
        return "Set the output(s) for the specified transform by adding relationship(s).";
    }

    @Override
    public void execute(Element args, Inputs inputs, Outputs outputs, XmlWriter w) throws Throwable {

        long uid = args.longValue("uid");
        Transform transform = TransformRepository.getInstance(executor()).get(uid);
        List<XmlDoc.Element> oes = args.elements("output");
        for (XmlDoc.Element oe : oes) {
            String id = oe.value("id");
            String cid = oe.value("cid");
            if (id != null) {
                transform.removeOutput(id);
            } else if (cid != null) {
                transform.removeOutput(AssetUtils.assetIdFromCiteableId(executor(), cid));
            } else {
                throw new Exception("Expecting output/id or output/cid. Found none.");
            }
        }
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
