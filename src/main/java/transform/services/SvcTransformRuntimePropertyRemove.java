package transform.services;

import transform.Transform;
import transform.TransformRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.LongType;
import arc.mf.plugin.dtype.StringType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformRuntimePropertyRemove extends PluginService {

    public static final String SERVICE_NAME = "transform.runtime.property.remove";

    private Interface _defn;

    public SvcTransformRuntimePropertyRemove() {
        _defn = new Interface();
        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform.", 1, 1));
        _defn.add(new Interface.Element("name", StringType.DEFAULT, "The name of the runtime property.", 1, 1));
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
        return "Removes the specified runtime property.";
    }

    @Override
    public void execute(Element args, Inputs arg1, Outputs arg2, XmlWriter arg3) throws Throwable {
        long uid = args.longValue("uid");
        String name = args.value("name");
        Transform t = TransformRepository.getInstance(executor()).get(uid);
        t.removeRuntimeProperty(name);
        t.commitChanges();
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
