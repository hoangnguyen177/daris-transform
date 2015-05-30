package transform.services;

import transform.TransformProvider;
import transform.TransformProviderRegistry;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.EnumType;
import arc.mf.plugin.dtype.StringType;
import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformProviderUserSettingsGet extends PluginService {

    private static final String SERVICE_NAME = "transform.provider.user.settings.get";
    private Interface _defn;

    public SvcTransformProviderUserSettingsGet() {
        _defn = new Interface();
        _defn.add(new Interface.Element("type", new EnumType(TransformProviderRegistry.providerTypes().toArray(
                new String[0])), "The type of the transform.", 1, 1));
        _defn.add(new Interface.Element("domain", StringType.DEFAULT, "The name of the authentication domain.", 1, 1));
        _defn.add(new Interface.Element("user", StringType.DEFAULT, "The name of the user.", 1, 1));
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
        return "Gets the given user's user-specific settings for the transform provider.";
    }

    @Override
    public void execute(Element args, Inputs arg1, Outputs arg2, XmlWriter w) throws Throwable {
        String type = args.value("type");
        String domain = args.value("domain");
        String user = args.value("user");
        TransformProvider tp = TransformProviderRegistry.getTransformProviderInstance(type, executor());
        XmlDoc.Element use = tp.userSpecificSettings(domain, user);
        if (use != null) {
            w.push("settings", new String[] { "domain", domain, "user", user });
            w.add(use, true);
            w.pop();
        }
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
