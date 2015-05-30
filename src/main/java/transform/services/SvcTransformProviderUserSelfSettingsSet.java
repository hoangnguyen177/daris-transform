package transform.services;

import transform.TransformProvider;
import transform.TransformProviderRegistry;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.EnumType;
import arc.mf.plugin.dtype.XmlDocType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlDocMaker;
import arc.xml.XmlWriter;

public class SvcTransformProviderUserSelfSettingsSet extends PluginService {

    private static final String SERVICE_NAME = "transform.provider.user.self.settings.set";
    private Interface _defn;

    public SvcTransformProviderUserSelfSettingsSet() {
        _defn = new Interface();
        _defn.add(new Interface.Element("type", new EnumType(TransformProviderRegistry.providerTypes().toArray(
                new String[0])), "The type of the transform provider.", 1, 1));
        Interface.Element se = new Interface.Element("settings", XmlDocType.DEFAULT,
                "The user specific settings for the current user.", 1, 1);
        se.setIgnoreDescendants(true);
        _defn.add(se);
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
        return "Sets the user-specific settings of given type of transform provider.";
    }

    @Override
    public void execute(Element args, Inputs arg1, Outputs arg2, XmlWriter arg3) throws Throwable {
        String type = args.value("type");
        TransformProvider tp = TransformProviderRegistry.getTransformProviderInstance(type, executor());
        XmlDocMaker dm = new XmlDocMaker(tp.userSpecificSettingsRootName());
        dm.add(args.element("settings"), false);
        tp.setUserSpecificSettings(dm.root());
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
