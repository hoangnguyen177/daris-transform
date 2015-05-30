package transform.services;

import transform.TransformProvider;
import transform.TransformProviderRegistry;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.EnumType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlDoc;
import arc.xml.XmlWriter;

public class SvcTransformProviderUserSelfSettingsGet extends PluginService {

    private static final String SERVICE_NAME = "transform.provider.user.self.settings.get";
    private Interface _defn;

    public SvcTransformProviderUserSelfSettingsGet() {
        _defn = new Interface();
        _defn.add(new Interface.Element("type", new EnumType(TransformProviderRegistry.providerTypes().toArray(
                new String[0])), "The type of the transform.", 1, 1));
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
        return "Gets the user-specific settings of given type of transform provider.";
    }

    @Override
    public void execute(Element args, Inputs arg1, Outputs arg2, XmlWriter w) throws Throwable {
        String type = args.value("type");
        TransformProvider tp = TransformProviderRegistry.getTransformProviderInstance(type, executor());
        XmlDoc.Element use = tp.userSpecificSettings();
        if (use != null) {
            w.push("settings");
            w.add(use, true);
            w.pop();
        }
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
