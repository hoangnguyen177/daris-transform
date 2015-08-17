package transform.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import transform.Transform;
import transform.Transform.Status;
import transform.TransformProviderRegistry;
import transform.TransformRepository;
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

public class SvcTransformDestroyAll extends PluginService {

    public static final String SERVICE_NAME = "transform.destroy.all";
    public static final String SERVICE_DESCRIPTION = "Destroy all the transform (owned by the user) in the specified states.";

    private Interface _defn;

    public SvcTransformDestroyAll() {
        _defn = new Interface();
        _defn.add(new Interface.Element("status", new EnumType(
                new Transform.Status.State[] { Transform.Status.State.FAILED,
                        Transform.Status.State.TERMINATED,
                        Transform.Status.State.UNKNOWN }),
                "Transforms in which state will be destroyed.", 1, 3));
        _defn.add(new Interface.Element(
                "type",
                new EnumType(TransformProviderRegistry.providerTypes().toArray(
                        new String[0])),
                "The transform provider type. If specified, destroys only the transforms with the given (provider) type.",
                0, 1));
        Interface.Element definition = new Interface.Element(
                "definition",
                LongType.POSITIVE_ONE,
                "The unique id of the transform definition. If specified, destroys only the transforms with the given definition.",
                0, 1);
        definition
                .add(new Interface.Attribute(
                        "version",
                        new IntegerType(0, Integer.MAX_VALUE),
                        "The version of the transform definition. A value of zero means the latest version. Defaults to latest.",
                        0));
        _defn.add(definition);
        _defn.add(new Interface.Element(
                "ignore-dependants",
                BooleanType.DEFAULT,
                "Set to true to ignore the dependants. Defaults to false, which means if there is any dependant, exception will be thrown.",
                0, 1));
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
        return SERVICE_DESCRIPTION;
    }

    @Override
    public void execute(Element args, Inputs inputs, Outputs outputs,
            XmlWriter w) throws Throwable {
        Set<Transform.Status.State> states = parseStatus(args);
        String type = args.value("type");
        String defnUid = args.value("definition");
        String defnVersion = args.value("definition/@version");
        boolean ignoreDependants = args
                .booleanValue("ignore-dependants", false);

        StringBuilder sb = new StringBuilder();
        sb.append("(" + Transform.DOC_TYPE + " has value)");
        sb.append(" and (created by '");
        sb.append(Session.user().domain());
        sb.append(":");
        sb.append(Session.user().name());
        sb.append("')");
        if (type != null) {
            sb.append(" and (xpath(" + Transform.DOC_TYPE
                    + "/type) as string = '" + type + "')");
        }
        if (states != null) {
            sb.append(" and (");
            boolean first = true;
            for (Status.State state : states) {
                if (first) {
                    first = false;
                } else {
                    sb.append(" or ");
                }
                sb.append("(xpath(" + Transform.DOC_TYPE
                        + "/status) as string = '" + state + "')");
            }
            sb.append(")");
        }
        if (defnUid != null) {
            sb.append(" and (xpath(" + Transform.DOC_TYPE + "/definition)="
                    + defnUid + ")");
        }
        if (defnVersion != null) {
            sb.append(" and (xpath(" + Transform.DOC_TYPE
                    + "/definition/@version)=" + defnVersion + ")");
        }
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("size", "infinity");
        dm.add("where", sb.toString());
        dm.add("action", "get-value");
        dm.add("xpath", new String[] { "ename", "uid" }, "meta/"
                + Transform.DOC_TYPE + "/uid");
        XmlDoc.Element re = executor().execute("asset.query", dm.root());
        List<Long> uids = re.longValues("asset/uid");
        if (uids != null) {
            for (long uid : uids) {
                TransformRepository.getInstance(executor()).delete(uid, ignoreDependants, false);
            }
        }
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

    private static Set<Transform.Status.State> parseStatus(XmlDoc.Element args)
            throws Throwable {
        Collection<String> ss = args.values("status");
        if (ss != null) {
            Set<Transform.Status.State> states = new HashSet<Transform.Status.State>();
            for (String s : ss) {
                Transform.Status.State state = Transform.Status.State
                        .fromString(s);
                if (state != null) {
                    states.add(state);
                }
            }
            if (!states.isEmpty()) {
                return states;
            }
        }
        return null;
    }

}
