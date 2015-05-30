package transform.services;

import transform.TransformDefinitionRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.BooleanType;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformDefinitionDestroy extends PluginService {

    public static final String SERVICE_NAME = "transform.definition.destroy";

    private Interface _defn;

    public SvcTransformDefinitionDestroy() {
        _defn = new Interface();
        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform definition.", 1,
                1));
        _defn.add(new Interface.Element(
                "ignore-dependants",
                BooleanType.DEFAULT,
                "Set to true to ignore the dependants. Defaults to false, which means if there is any dependant, exception will be thrown.",
                0, 1));
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
        return "Destroys a transform definition.";
    }

    @Override
    public void execute(Element args, Inputs inputs, Outputs outputs, XmlWriter w) throws Throwable {

        long uid = args.longValue("uid");
        boolean ignoreDependants = args.booleanValue("ignore-dependants", false);
        TransformDefinitionRepository.getInstance(executor()).delete(uid, ignoreDependants);
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
