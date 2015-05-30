package transform;

import java.util.ArrayList;
import java.util.List;

import transform.mf.ACL;
import transform.mf.Asset;
import transform.mf.AssetUtils;
import transform.mf.User;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.PluginService.Input;
import arc.mf.plugin.ServiceExecutor;
import arc.xml.CanSaveToXml;
import arc.xml.XmlDoc;
import arc.xml.XmlDocMaker;
import arc.xml.XmlWriter;

/**
 * The abstract class for transform entity.
 * 
 * @author wilson
 * 
 */
public abstract class Entity implements CanSaveToXml, Asset {

    private long _uid;
    private String _name;
    private String _description;
    private String _providerType;
    private String _assetId;
    private int _version;
    private String _namespace;
    private User _creator;
    private XmlDoc.Element _ce;
    private List<ACL> _acls;
    private PluginService.Input _contentIn;

    /**
     * The constructor.
     * 
     * @param ae
     *            the XML element represents the entity asset.
     * @throws Throwable
     */
    protected Entity(XmlDoc.Element ae) throws Throwable {
        _assetId = ae.value("@id");
        _version = ae.intValue("@version", 0);
        _namespace = ae.value("namespace");
        _ce = ae.element("content");
        if (ae.elementExists("creator")) {
            _creator = new User(ae.value("creator/domain"), ae.value("creator/user"));
        }
        XmlDoc.Element te = ae.element("meta/" + docType());
        _uid = te.longValue("uid");
        _name = te.value("name");
        _description = te.value("description");
        _providerType = te.value("type");
        if (ae.elementExists("acl")) {
            List<XmlDoc.Element> acles = ae.elements("acl");
            List<ACL> acls = new ArrayList<ACL>(acles.size());
            for (XmlDoc.Element acle : acles) {
                acls.add(new ACL(acle));
            }
        }
    }

    /**
     * The constructor.
     * 
     * @param uid
     *            the unique identifier.
     * @param name
     *            the name of the entity.
     * @param description
     *            the description about the entity.
     * @param provider
     *            the transform provider.
     * @param namespace
     *            the asset namespace for the entity.
     * @param content
     * @throws Throwable
     */
    protected Entity(long uid, String name, String description, TransformProvider provider, String namespace,
            PluginService.Input content, List<ACL> acls) throws Throwable {
        _uid = uid;
        _name = name;
        _description = description;
        _providerType = provider.type();
        _assetId = null;
        _version = 0;
        _namespace = namespace;
        _contentIn = content;
        _acls = acls;
        _creator = User.self();
    }

    /**
     * The unique identifier of the entity.
     * 
     * @return
     */
    public final long uid() {
        return _uid;
    }

    /**
     * The name of the entity.
     * 
     * @return
     */
    public String name() {
        return _name;
    }

    /**
     * Sets the name of the entity.
     * 
     * @param name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * The description about the entity.
     * 
     * @return
     */
    public String description() {
        return _description;
    }

    /**
     * Sets the description about the entity.
     * 
     * @param description
     */
    public void setDescription(String description) {
        _description = description;
    }

    /**
     * The type of the transform provider.
     * 
     * @return
     */
    public final String providerType() {

        return _providerType;
    }

    /**
     * The primary XML document type associated with the entity.
     * 
     * @return
     */
    public abstract String docType();

    @Override
    public final String assetId() {
        return _assetId;
    }

    @Override
    public final int version() {
        return _version;
    }

    @Override
    public final String namespace() {
        return _namespace;
    }

    @Override
    public Input contentInput() {
        return _contentIn;
    }

    public void setContentInput(PluginService.Input input) {
        _contentIn = input;
    }

    @Override
    public PluginService.Output contentOutput() throws Throwable {

        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("id", new String[] { "version", Integer.toString(_version) }, _assetId);
        PluginService.Outputs outputs = new PluginService.Outputs(1);
        executor().execute("asset.get", dm.root(), null, outputs);
        if (outputs.output(0) != null && outputs.output(0).stream() != null) {
            return outputs.output(0);
        }
        return null;
    }

    @Override
    public XmlDoc.Element contentElement() {
        return _ce;
    }

    public boolean hasContent() {
        return _ce != null;
    }

    public boolean hasDependants() throws Throwable {
        return false;
    }

    /**
     * Check if the entity object has been created in the repository. If returns
     * true, indicates the entity is a uncommitted local object.
     * 
     * @return
     */
    public boolean created() {
        return _assetId != null;
    }

    /**
     * Commit the changes on the entity to the repository. If the entity is a
     * local uncommitted object, it will create one in the repository.
     * Otherwise, update it in the repository.
     * 
     * @throws Throwable
     */
    public Entity commitChanges() throws Throwable {
        Entity e = repository().save(this);
        _assetId = e.assetId();
        _namespace = e.namespace();
        _version = e.version();
        return e;
    }

    @Override
    public final void save(XmlWriter w) throws Throwable {
        save(w, true);
    }

    public abstract void save(XmlWriter w, boolean doc) throws Throwable;

    /**
     * The repository associates with the entity.
     * 
     * @return
     */
    protected abstract <T extends Entity> Repository<T> repository();

    /**
     * The service executor from the associated repository.
     * 
     * @return
     */
    protected ServiceExecutor executor() {
        return repository().executor();
    }

    @Override
    public void addRelationshipFrom(String relationship, String from) throws Throwable {
        AssetUtils.addRelationship(executor(), relationship, from, false, assetId());
    }

    @Override
    public void removeRelationshipFrom(String relationship, String from) throws Throwable {
        AssetUtils.removeRelationship(executor(), relationship, from, false, assetId());
    }

    @Override
    public void addRelationshipTo(String relationship, String to) throws Throwable {
        AssetUtils.addRelationship(executor(), relationship, assetId(), false, to);
    }

    @Override
    public void removeRelationshipTo(String relationship, String to) throws Throwable {
        AssetUtils.removeRelationship(executor(), relationship, assetId(), false, to);
    }

    @Override
    public List<ACL> acls() {
        return _acls;
    }

    @Override
    public void setAcls(List<ACL> acls) {
        _acls = acls;
    }

    @Override
    public User creator() {
        return _creator;
    }

}
