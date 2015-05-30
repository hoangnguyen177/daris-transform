package transform;

import java.util.List;
import java.util.Map;

import transform.event.TransformEvent;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.ServiceExecutor;
import arc.mf.plugin.event.SystemEventChannel;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlDocMaker;

/**
 * The transform repository.
 * 
 * @author Wei Liu (wliu1976@gmail.com)
 * 
 */
public class TransformRepository extends AbstractRepository<Transform> {

    @Override
    protected String getAssetId(long uid) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("where", "xpath(" + Transform.DOC_TYPE + "/uid)=" + uid);
        String assetId = executor().execute("asset.query", dm.root(), null, null).value("id");
        if (assetId == null) {
            throw new Exception("Transform (uid=" + uid + ") does not exist.");
        }
        return assetId;
    }

    private TransformRepository(ServiceExecutor executor) {
        super(executor);
    }

    /**
     * Gets the instance of the transform repository.
     * 
     * @return
     */
    public static TransformRepository getInstance(ServiceExecutor executor) {
        return new TransformRepository(executor);
    }

    @Override
    public String uidName() {
        return Transform.UID_NAME;
    }

    @Override
    protected Transform instantiate(Element ae) throws Throwable {
        String providerType = ae.value("meta/" + Transform.DOC_TYPE + "/type");
        return TransformProviderRegistry.getTransformProviderInstance(providerType, executor()).instantiateTransform(
                this, ae);
    }

    @Override
    public Transform save(Transform entity) throws Throwable {
        boolean updating = entity.created();
        Transform transform = super.save(entity);
        /*
         * Dispatch system events when the transform is created or updated.
         */
        if (updating) {
            SystemEventChannel.generate(new TransformEvent(transform.uid(), TransformEvent.Action.UPDATE));
        } else {
            SystemEventChannel.generate(new TransformEvent(transform.uid(), TransformEvent.Action.CREATE));
        }
        return transform;
    }

    @Override
    public void delete(Transform transform, boolean ignoreDependants) throws Throwable {
        delete(transform, ignoreDependants, false);
    }

    public void delete(Transform transform, boolean ignoreDependants, boolean ignoreStatus) throws Throwable {
        long uid = transform.uid();
        // transform.updateStatus();
        if (!ignoreStatus && transform.status().state() != Transform.Status.State.TERMINATED
                && transform.status().state() != Transform.Status.State.FAILED
                && transform.status().state() != Transform.Status.State.UNKNOWN) {
            throw new Exception("Cannot destroy transform " + transform.uid() + ". The transform state is "
                    + transform.status().state() + ".");
        }
        super.delete(transform, ignoreDependants);
        /*
         * Dispatch system event when the transform is destroyed.
         */
        SystemEventChannel.generate(new TransformEvent(uid, TransformEvent.Action.DESTROY));
    }

    public void delete(long uid, boolean ignoreDependants, boolean ignoreStatus) throws Throwable {
        delete(get(uid), ignoreDependants, ignoreStatus);
    }

    /**
     * Creates a transform in the repository.
     * 
     * @param defnUid
     *            the unique id of the transform definition.
     * @param defnVersion
     *            the version of the transform definition.
     * @param name
     *            the name of the transform.
     * @param description
     *            the description for the transform.
     * @param params
     *            the parameters for the transform.
     * @param properties
     *            the runtime properties for the transform.
     * @param input
     *            the content input stream for the transform.
     * @return
     * @throws Throwable
     */
    public Transform create(long defnUid, int defnVersion, String name, String description, List<Parameter> params,
            Map<String, String> properties, PluginService.Input input) throws Throwable {
        TransformDefinition defn = TransformDefinitionRepository.getInstance(executor()).get(defnUid, defnVersion);
        if (defn == null) {
            throw new Exception("Transform definition " + defnUid + " does not exist.");
        }
        Transform transform = TransformProviderRegistry.getTransformProviderInstance(defn.providerType(), executor())
                .createTransform(this, defn, name, description, params, properties, input);
        return save(transform);
    }

    @Override
    protected void beforeCommit(Transform t) throws Throwable {
        TransformProviderRegistry.getTransformProviderInstance(t.providerType(), executor()).beforeCommit(t);
    }

    @Override
    protected void beforeDelete(Transform t) throws Throwable {
        TransformProviderRegistry.getTransformProviderInstance(t.providerType(), executor()).beforeDelete(t);
    }
}
