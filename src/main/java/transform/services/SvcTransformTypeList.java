package transform.services;

import java.util.Collection;

import transform.TransformProviderRegistry;
import arc.mf.plugin.PluginService;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformTypeList extends PluginService {

    private static final String SERVICE_NAME = "transform.type.list";
    private Interface _defn;

    public SvcTransformTypeList() {
        _defn = new Interface();
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
        return "Lists the types of the tranform are supported.";
    }

    @Override
    public void execute(Element args, Inputs arg1, Outputs arg2, XmlWriter w) throws Throwable {
        Collection<String> types = TransformProviderRegistry.providerTypes();
        if (types != null) {
            for (String type : types) {
                w.add("type", new String[] { "provider",
                        TransformProviderRegistry.getTransformProviderClass(type).getName() }, type);
            }
        }
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
