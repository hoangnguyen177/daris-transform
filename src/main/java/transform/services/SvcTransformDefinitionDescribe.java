package transform.services;

import transform.TransformDefinition;
import transform.TransformDefinitionRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.PluginService.Interface.Attribute;
import arc.mf.plugin.dtype.AssetType;
import arc.mf.plugin.dtype.IntegerType;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformDefinitionDescribe extends PluginService {

    public static final String SERVICE_NAME = "transform.definition.describe";

    private Interface _defn;

    public SvcTransformDefinitionDescribe() {
        _defn = new Interface();
        Interface.Element uid = new Interface.Element("uid", LongType.POSITIVE_ONE,
                "The unique id of the transform definition.", 0, 1);
        uid.add(new Attribute("version", IntegerType.POSITIVE,
                "the version of the transform definition. Defaults to latest.", 0));
        _defn.add(uid);
        Interface.Element id = new Interface.Element("id", AssetType.DEFAULT,
                "The asset id of the transform definition.", 0, 1);
        id.add(new Attribute("version", IntegerType.POSITIVE,
                "the version of the transform definition. Defaults to latest.", 0));
        _defn.add(id);

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
        return "Describes the transform definition.";
    }

    @Override
    public void execute(Element args, Inputs in, Outputs out, XmlWriter w) throws Throwable {
        boolean idExists = args.elementExists("id");
        boolean uidExists = args.elementExists("uid");
        if (idExists && uidExists) {
            throw new Exception("Expecting either uid or id. Found both.");
        }
        if (!idExists && !uidExists) {
            throw new Exception("Expecting either uid or id. Found none.");
        }

        TransformDefinitionRepository repo = TransformDefinitionRepository.getInstance(executor());
        TransformDefinition td = idExists ? repo.get(args.value("id"), args.intValue("id/version", 0)) : repo.get(
                args.longValue("uid"), args.intValue("uid/version", 0));
        w.push("transform-definition",
                new String[] { "asset", td.assetId(), "version", Integer.toString(td.version()) });
        td.save(w, false);
        if (td.hasContent()) {
            w.add(td.contentElement());
        }
        w.pop();
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
