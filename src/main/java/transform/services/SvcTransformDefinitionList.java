package transform.services;

import java.util.List;

import transform.TransformDefinition;
import transform.TransformProviderRegistry;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.EnumType;
import arc.mf.plugin.dtype.IntegerType;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlDocMaker;
import arc.xml.XmlWriter;

public class SvcTransformDefinitionList extends PluginService {

    public static final String SERVICE_NAME = "transform.definition.list";

    private Interface _defn;

    public SvcTransformDefinitionList() {
        _defn = new Interface();
        _defn.add(new Interface.Element("type", new EnumType(TransformProviderRegistry.providerTypes().toArray(
                new String[0])),
                "The transform type. If present, only the transform definitions of this type will be listed.", 0, 1));
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
        return "Lists the transform definitions.";
    }

    @Override
    public void execute(Element args, Inputs in, Outputs out, XmlWriter w) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        if (args.element("size") != null) {
            dm.add("size", args.intValue("size"));
        }
        if (args.element("idx") != null) {
            dm.add("idx", args.longValue("idx"));
        }

        StringBuilder sb = new StringBuilder();
        sb.append(TransformDefinition.DOC_TYPE + " has value");
        String type = args.value("type");
        if (type != null) {
            sb.append(" and (xpath(" + TransformDefinition.DOC_TYPE + "/type)='" + type.toString() + "')");
        }
        dm.add("where", sb.toString());
        dm.add("action", "get-value");
        dm.add("xpath", new String[] { "ename", "uid" }, "meta/" + TransformDefinition.DOC_TYPE + "/uid");
        dm.add("xpath", new String[] { "ename", "type" }, "meta/" + TransformDefinition.DOC_TYPE + "/type");
        dm.add("xpath", new String[] { "ename", "name" }, "meta/" + TransformDefinition.DOC_TYPE + "/name");
        XmlDoc.Element r = executor().execute("asset.query", dm.root());
        List<XmlDoc.Element> aes = r.elements("asset");
        if (aes != null) {
            for (XmlDoc.Element ae : aes) {
                w.add("transform-definition", new String[] { "uid", ae.value("uid"), "asset", ae.value("@id"), "type",
                        ae.value("type"), "name", ae.value("name") });
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
