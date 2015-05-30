package transform.services;

import transform.TransformProvider;
import transform.TransformProviderRegistry;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.EnumType;
import arc.mf.plugin.dtype.StringType;
import arc.mf.plugin.dtype.XmlDocType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlDocMaker;
import arc.xml.XmlWriter;

public class SvcTransformProviderUserSettingsSet extends PluginService {
    private static final String SERVICE_NAME = "transform.provider.user.settings.set";
    private Interface _defn;

    public SvcTransformProviderUserSettingsSet() {
        _defn = new Interface();
        _defn.add(new Interface.Element("type", new EnumType(TransformProviderRegistry.providerTypes().toArray(
                new String[0])), "The type of the transform provider.", 1, 1));

        _defn.add(new Interface.Element("domain", StringType.DEFAULT, "The name of the authentication domain.", 1, 1));
        _defn.add(new Interface.Element("user", StringType.DEFAULT, "The name of the user.", 1, 1));

        Interface.Element se = new Interface.Element("settings", XmlDocType.DEFAULT,
                "The user specific settings for the current user.", 1, 1);
        se.setIgnoreDescendants(true);
        _defn.add(se);
    }

    @Override
    public Access access() {
        return ACCESS_ADMINISTER;
    }

    @Override
    public Interface definition() {
        return _defn;
    }

    @Override
    public String description() {
        return "Sets the given user's user-specific settings for the transform provider.";
    }

    @Override
    public void execute(Element args, Inputs arg1, Outputs arg2, XmlWriter w) throws Throwable {
        String type = args.value("type");
        String domain = args.value("domain");
        String user = args.value("user");
        TransformProvider tp = TransformProviderRegistry.getTransformProviderInstance(type, executor());
        XmlDocMaker dm = new XmlDocMaker(tp.userSpecificSettingsRootName());
        dm.add(args.element("settings"), false);
        tp.setUserSpecificSettings(domain, user, dm.root());
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
