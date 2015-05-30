package transform.services;

import transform.TransformProviderRegistry;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.EnumType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlDocDefinition;
import arc.xml.XmlWriter;

public class SvcTransformProviderUserSettingsDefinitionGet extends PluginService {

    private static final String SERVICE_NAME = "transform.provider.user.settings.definition.get";
    private Interface _defn;

    public SvcTransformProviderUserSettingsDefinitionGet() {
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

        return "Gets the XML definition for the user specific settings of the given type of transform provider.";
    }

    @Override
    public void execute(Element args, Inputs arg1, Outputs arg2, XmlWriter w) throws Throwable {
        XmlDocDefinition.Element de = TransformProviderRegistry.getTransformProviderInstance(args.value("type"),
                executor()).userSpecificSettingsDefinition();
        de.describe(w, true);
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
