package transform.services;

import java.util.List;

import transform.Transform;
import transform.TransformRepository;
import transform.mf.AssetUtils;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.AssetType;
import arc.mf.plugin.dtype.CiteableIdType;
import arc.mf.plugin.dtype.LongType;
import arc.mf.plugin.dtype.XmlDocType;
import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformOutputAdd extends PluginService {

    private static final String SERVICE_NAME = "transform.output.add";
    private Interface _defn;

    public SvcTransformOutputAdd() {
        _defn = new Interface();
        addToInterface(_defn);
    }

    public static void addToInterface(Interface defn) {
        defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform.", 1, 1));
        Interface.Element oe = new Interface.Element("output", XmlDocType.DEFAULT, "The output of the transform.", 1,
                Integer.MAX_VALUE);
        oe.add(new Interface.Element("id", AssetType.DEFAULT,
                "The asset id of the output. Must be specified if cid is not presented.", 0, 1));
        oe.add(new Interface.Element("cid", CiteableIdType.DEFAULT,
                "The citeable id of the output. Must be specified if id is not presented.", 0, 1));
        defn.add(oe);
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
                transform.addOutput(id);
            } else if (cid != null) {
                transform.addOutput(AssetUtils.assetIdFromCiteableId(executor(), cid));
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
