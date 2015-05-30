package transform.services;

import transform.TransformRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.BooleanType;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformDestroy extends PluginService {

    private static final String SERVICE_NAME = "transform.destroy";
    private Interface _defn;

    public SvcTransformDestroy() {
        _defn = new Interface();
        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform.", 1, 1));
        _defn.add(new Interface.Element(
                "ignore-dependants",
                BooleanType.DEFAULT,
                "Set to true to ignore the dependants. Defaults to false, which means if there is any dependant, exception will be thrown.",
                0, 1));
        _defn.add(new Interface.Element(
                "ignore-status",
                BooleanType.DEFAULT,
                "Set to true to proceed the deletion even if the transform is running, in the state of pending, runnning or suspended. Defaults to false. Exception will be thrown if the transform is running.",
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
        return "Destroys a transform instance.";
    }

    @Override
    public void execute(Element args, Inputs in, Outputs out, XmlWriter w) throws Throwable {
        long uid = args.longValue("uid");
        boolean ignoreDependants = args.booleanValue("ignore-dependants", false);
        boolean ignoreStatus = args.booleanValue("ignore-status", false);
        TransformRepository.getInstance(executor()).delete(uid, ignoreDependants, ignoreStatus);
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
