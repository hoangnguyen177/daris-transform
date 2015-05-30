package transform;

import transform.mf.ACL;
import transform.mf.UniqueId;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.ServiceExecutor;
import arc.xml.XmlDoc;
import arc.xml.XmlDocMaker;
import arc.xml.XmlDocWriter;

/**
 * The abstract class implements Repository interface.
 * 
 * @author wilson
 * 
 * @param <T>
 */
public abstract class AbstractRepository<T extends Entity> implements Repository<T> {

    private ServiceExecutor _executor;

    /**
     * The constructor.
     * 
     * @param executor
     *            The service executor.
     */
    protected AbstractRepository(ServiceExecutor executor) {
        _executor = executor;
    }

    @Override
    public ServiceExecutor executor() {
        return _executor;
    }

    /**
     * Retrieves the asset identifier of the entity.
     * 
     * @param uid
     *            the unique id of the object.
     * @return
     * @throws Throwable
     */
    protected abstract String getAssetId(long uid) throws Throwable;

    /**
     * Instantiate the entity from the given XML element.
     * 
     * @param ae
     *            the XML element represents the entity asset.
     * @return
     * @throws Throwable
     */
    protected abstract T instantiate(XmlDoc.Element ae) throws Throwable;

    /**
     * Gets the XML elements represents the entity's asset meta data.
     * 
     * @param assetId
     *            the asset identifier of the entity
     * @param version
     *            the version of the entity
     * 
     * @return
     * @throws Throwable
     */
    protected XmlDoc.Element getAssetMeta(String assetId, int version) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("id", new String[] { "version", Integer.toString(version) }, assetId);
        return executor().execute("asset.get", dm.root()).element("asset");
    }

    /**
     * Retrieves the object by asset identifier and version.
     * 
     * @param assetId
     *            the asset identifier
     * @param version
     *            the version
     * @return
     * @throws Throwable
     */
    public T get(String assetId, int version) throws Throwable {
        return instantiate(getAssetMeta(assetId, version));
    }

    /**
     * Retrieves the object by asset identifier
     * 
     * @param assetId
     *            the asset identifier
     * @return
     * @throws Throwable
     */
    public T get(String assetId) throws Throwable {
        return get(assetId, 0);
    }

    @Override
    public T get(long uid, int version) throws Throwable {
        return get(getAssetId(uid), version);
    }

    @Override
    public T get(long uid) throws Throwable {
        return get(getAssetId(uid), 0);
    }

    /**
     * The name of the unique identifier. It is used by the uniqueid.next
     * service to generate the unique identifier.
     * 
     * @return
     */
    public abstract String uidName();

    @Override
    public long uidNext() throws Throwable {
        return UniqueId.next(uidName());
    }

    @Override
    public T save(T entity) throws Throwable {

        beforeCommit(entity);
        XmlDocMaker dm = new XmlDocMaker("args");
        XmlDocWriter w = new XmlDocWriter(dm);
        if (entity.created()) {
            w.add("id", entity.assetId());
            w.add("replace-all-meta", true);
            w.push("meta", new String[] { "action", "replace" });
        } else {
            w.add("namespace", new String[] { "create", "true" }, entity.namespace());
            w.push("meta");
        }
        /*
         * metadata
         */
        entity.save(w);
        w.pop();
        /*
         * acls
         */
        if (entity.acls() != null) {
            for (ACL acl : entity.acls()) {
                acl.save(w);
            }
        }
        XmlDoc.Element re = executor().execute(entity.created() ? "asset.set" : "asset.create", dm.root(),
                entity.contentInput() == null ? null : new PluginService.Inputs(entity.contentInput()), null);
        return get(entity.created() ? entity.assetId() : re.value("id"));
    }

    protected void beforeCommit(T entity) throws Throwable {

    }

    protected void beforeDelete(T entity) throws Throwable {

    }

    @Override
    public void delete(T entity) throws Throwable {
        delete(entity, false);
    }

    public void delete(T entity, boolean ignoreDependants) throws Throwable {
        beforeDelete(entity);
        if (!ignoreDependants && entity.hasDependants()) {
            throw new Exception("Failed to destroy " + entity.getClass().getName() + " " + entity.uid()
                    + " because it is referred by other entities.");
        }
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("id", entity.assetId());
        executor().execute("asset.destroy", dm.root());
    }

    public void delete(long uid) throws Throwable {
        delete(uid, false);
    }

    public void delete(long uid, boolean ignoreDependants) throws Throwable {
        T entity = get(uid);
        if (entity != null) {
            delete(entity, ignoreDependants);
        }
    }

}
