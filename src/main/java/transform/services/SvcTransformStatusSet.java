package transform.services;

import transform.Transform;
import transform.Transform.Status;
import transform.TransformRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.EnumType;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformStatusSet extends PluginService {

    private static final String SERVICE_NAME = "transform.status.set";
    private Interface _defn;

    public SvcTransformStatusSet() {
        _defn = new Interface();

        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform.", 1, 1));

        _defn.add(new Interface.Element("status", new EnumType(Transform.Status.State.stringValues()),
                "The state of the transform.", 1, 1));
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
        return "Sets the progress of the transform.";
    }

    @Override
    public void execute(Element args, Inputs arg1, Outputs arg2, XmlWriter w) throws Throwable {
        long uid = args.longValue("uid");
        Status.State state = Status.State.fromString(args.value("status"));
        Transform t = TransformRepository.getInstance(executor()).get(uid);
        t.setStatus(state);
        t.commitChanges();
        
    	if (state == Status.State.TERMINATED || state == Status.State.FAILED || state == Status.State.UNKNOWN) {
    		t.destroyToken();
    	}
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
