package transform.services;

import java.util.List;

import transform.Transform;
import transform.TransformRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.LongType;
import arc.mf.plugin.dtype.StringType;
import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformRuntimePropertySet extends PluginService {

    private static final String SERVICE_NAME = "transform.runtime.property.set";
    private Interface _defn;

    public SvcTransformRuntimePropertySet() {
        _defn = new Interface();
        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform.", 1, 1));

        Interface.Element e = new Interface.Element("property", StringType.DEFAULT,
                "The runtime property of the transform.", 1, Integer.MAX_VALUE);
        e.add(new Interface.Attribute("name", StringType.DEFAULT, "The name of the runtime property.", 1));
        _defn.add(e);
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
        return "sets one or more runtime properties of a transform.";
    }

    @Override
    public void execute(Element args, Inputs arg1, Outputs arg2, XmlWriter arg3) throws Throwable {
        long uid = args.longValue("uid");
        List<XmlDoc.Element> pes = args.elements("property");
        Transform t = TransformRepository.getInstance(executor()).get(uid);
        for (XmlDoc.Element pe : pes) {
            t.setRuntimeProperty(pe.value("@name"), pe.value());
        }
        t.commitChanges();
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
