package transform.services;

import java.util.List;

import transform.Transform;
import transform.TransformProviderRegistry;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.Session;
import arc.mf.plugin.dtype.BooleanType;
import arc.mf.plugin.dtype.EnumType;
import arc.mf.plugin.dtype.IntegerType;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlDocMaker;
import arc.xml.XmlWriter;

public class SvcTransformList extends PluginService {

    public static final String SERVICE_NAME = "transform.list";
    private Interface _defn;

    public SvcTransformList() {
        _defn = new Interface();
        _defn.add(new Interface.Element(
                "type",
                new EnumType(TransformProviderRegistry.providerTypes().toArray(new String[0])),
                "The transform provider type. If specified, only the transform instances with the specified provider will be listed.",
                0, 1));
        _defn.add(new Interface.Element(
                "status",
                new EnumType(Transform.Status.State.values()),
                "The status of the transform instance. If specified, only the instances on the specified status will be listed. ",
                0, 1));
        Interface.Element definition = new Interface.Element("definition", LongType.POSITIVE_ONE,
                "The unique id of the transform definition.", 0, 1);
        definition
                .add(new Interface.Attribute(
                        "version",
                        new IntegerType(0, Integer.MAX_VALUE),
                        "The version of the transform definition. A value of zero means the latest version. Defaults to latest.",
                        0));
        _defn.add(definition);
        _defn.add(new Interface.Element("size", IntegerType.DEFAULT, "The size of the list. Defaults to 100.", 0, 1));
        _defn.add(new Interface.Element("idx", LongType.DEFAULT,
                "Absolute cursor position. Starts from 1. If used, the cursor will be positioned starting at 'idx'.",
                0, 1));
        _defn.add(new Interface.Element("self", BooleanType.DEFAULT, "List only self owned/created transforms. Defaults to true.", 0,1));
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
        return "Lists the transforms.";
    }

    @Override
    public void execute(Element args, Inputs in, Outputs out, XmlWriter w) throws Throwable {
        String idx = args.value("idx");
        String size = args.value("size");
        String type = args.value("type");
        String status = args.value("status");
        String defnUid = args.value("definition");
        String defnVersion = args.value("definition/@version");
        boolean self = args.booleanValue("self", true);

        XmlDocMaker dm = new XmlDocMaker("args");
        if (size != null) {
            dm.add("size", size);
        }
        if (idx != null) {
            dm.add("idx", idx);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(" + Transform.DOC_TYPE + " has value)");
        if(self){
            sb.append(" and (created by '");
            sb.append(Session.user().domain());
            sb.append(":");
            sb.append(Session.user().name());
            sb.append("')");
        }
        if (type != null) {
            sb.append(" and (xpath(" + Transform.DOC_TYPE + "/type) as string = '" + type + "')");
        }
        if (status != null) {
            sb.append(" and (xpath(" + Transform.DOC_TYPE + "/status) as string = '" + status + "')");
        }
        if (defnUid != null) {
            sb.append(" and (xpath(" + Transform.DOC_TYPE + "/definition)=" + defnUid + ")");
        }
        if (defnVersion != null) {
            sb.append(" and (xpath(" + Transform.DOC_TYPE + "/definition/@version)=" + defnVersion + ")");
        }
        dm.add("where", sb.toString());
        dm.add("action", "get-value");
        dm.add("xpath", new String[] { "ename", "uid" }, "meta/" + Transform.DOC_TYPE + "/uid");
        dm.add("xpath", new String[] { "ename", "name" }, "meta/" + Transform.DOC_TYPE + "/name");
        dm.add("xpath", new String[] { "ename", "status" }, "meta/" + Transform.DOC_TYPE + "/status");
        dm.add("xpath", new String[] { "ename", "type" }, "meta/" + Transform.DOC_TYPE + "/type");
        XmlDoc.Element re = executor().execute("asset.query", dm.root());
        List<XmlDoc.Element> aes = re.elements("asset");
        if (aes != null) {
            for (XmlDoc.Element ae : aes) {
                w.add("transform",
                        new String[] { "uid", ae.value("uid"), "asset", ae.value("@id"), "type", ae.value("type"),
                                "name", ae.value("name"), "status", ae.value("status") });
            }
        }
        XmlDoc.Element ce = re.element("cursor");
        if (ce != null) {
            w.add(ce);
        }
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
