package transform.services;

import transform.Transform;
import transform.TransformRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformReset extends PluginService {

    private static final String SERVICE_NAME = "transform.reset";
    private Interface _defn;

    public SvcTransformReset() {
        _defn = new Interface();
        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform.", 1, 1));
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
        return "resets a transform to initial/pending state.";
    }

    @Override
    public void execute(Element args, Inputs in, Outputs out, XmlWriter w) throws Throwable {
        long uid = args.longValue("uid");
        Transform t = TransformRepository.getInstance(executor()).get(uid);
        if (t.status().state() != Transform.Status.State.TERMINATED) {
            t.terminate();
        }
        t.reset();
        t.commitChanges();
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
