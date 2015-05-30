package transform.services;

import java.util.List;

import transform.Transform;
import transform.TransformRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.IntegerType;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlDocMaker;
import arc.xml.XmlWriter;

public class SvcTransformOutputList extends PluginService {

    private static final String SERVICE_NAME = "transform.output.list";
    private Interface _defn;

    public SvcTransformOutputList() {
        _defn = new Interface();
        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform.", 1, 1));
        _defn.add(new Interface.Element("size", IntegerType.DEFAULT, "The size of the list. Defaults to 100.", 0, 1));
        _defn.add(new Interface.Element("idx", LongType.DEFAULT,
                "Absolute cursor position. Starts from 1. If used, the cursor will be positioned starting at 'idx'.",
                0, 1));
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
        return "Lists the outputs of transform.";
    }

    @Override
    public void execute(Element args, Inputs inputs, Outputs outputs, XmlWriter w) throws Throwable {
        long uid = args.longValue("uid");
        Transform t = TransformRepository.getInstance(executor()).get(uid);
        if (t == null) {
            throw new Exception("Transform " + uid + " does not exist.");
        }
        int idx = args.intValue("idx", 1);
        int size = args.intValue("size", 100);

        // query for related outputs
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("where", "related to{" + Transform.OUTPUT_RELATIONSHIP + "} (id=" + t.assetId() + ")");
        dm.add("action", "get-values");
        dm.add("xpath", new String[] { "ename", "cid" }, "cid");
        if (size > 1) {
            dm.add("size", size);
        }
        if (idx > 0) {
            dm.add("idx", idx);
        }
        XmlDoc.Element r = executor().execute("asset.query", dm.root());
        List<XmlDoc.Element> aes = r.elements("asset");
        if (aes != null && !aes.isEmpty()) {
            for (XmlDoc.Element ae : aes) {
                w.add("id", new String[] { "cid", ae.value("cid") }, ae.value("@id"));
            }
        }
        if (r.element("cursor") != null) {
            w.add(r.element("cursor"));
        }
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
