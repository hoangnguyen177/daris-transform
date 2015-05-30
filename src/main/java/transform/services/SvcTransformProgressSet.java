package transform.services;

import transform.Transform;
import transform.TransformRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.IntegerType;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformProgressSet extends PluginService {

    private static final String SERVICE_NAME = "transform.progress.set";
    private Interface _defn;

    public SvcTransformProgressSet() {
        _defn = new Interface();

        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "The unique id of the transform.", 1, 1));

        Interface.Element pe = new Interface.Element("progress", IntegerType.POSITIVE,
                "A positive integer indicates the progress.", 1, 1);
        pe.add(new Interface.Attribute("total", IntegerType.POSITIVE, "A positive number represents the total.", 1));
        _defn.add(pe);
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
        return "Sets the progress of the transform.";
    }

    @Override
    public void execute(Element args, Inputs arg1, Outputs arg2, XmlWriter w) throws Throwable {
        long uid = args.longValue("uid");
        int progress = args.intValue("progress");
        int total = args.intValue("progress/@total");
        if (total < progress) {
            throw new Exception("The progress number should be less than or equal to the total number.");
        }
        Transform t = TransformRepository.getInstance(executor()).get(uid);
        t.setProgress(progress, total);
        t.commitChanges();
    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
