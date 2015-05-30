package transform.mf;

import arc.mf.plugin.ServiceExecutor;
import arc.xml.XmlDocMaker;

public class AssetUtils {

    public static String citeableIdFromAssetId(ServiceExecutor executor, String assetId) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("id", assetId);
        return executor.execute("asset.get", dm.root()).value("asset/cid");
    }

    public static String assetIdFromCiteableId(ServiceExecutor executor, String citeableId) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("cid", citeableId);
        return executor.execute("asset.get", dm.root()).value("asset/@id");
    }

    public static void addRelationship(ServiceExecutor executor, String relationship, String fromId,
            boolean fromIdCiteable, String toAssetId) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add(fromIdCiteable ? "cid" : "id", fromId);
        dm.add("to", new String[] { "relationship", relationship }, toAssetId);
        executor.execute("asset.relationship.add", dm.root());
    }

    public static void removeRelationship(ServiceExecutor executor, String relationship, String fromId,
            boolean fromIdCiteable, String toAssetId) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add(fromIdCiteable ? "cid" : "id", fromId);
        dm.add("to", new String[] { "relationship", relationship }, toAssetId);
        executor.execute("asset.relationship.remove", dm.root());
    }

}
