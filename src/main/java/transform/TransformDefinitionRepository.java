package transform;

import java.util.List;

import arc.mf.plugin.PluginService;
import arc.mf.plugin.ServiceExecutor;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlDocMaker;

/**
 * The transform definition repository.
 * 
 * @author Wei Liu (wliu1976@gmail.com)
 * 
 */
public class TransformDefinitionRepository extends AbstractRepository<TransformDefinition> {

    @Override
    protected String getAssetId(long uid) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("where", "xpath(" + TransformDefinition.DOC_TYPE + "/uid)=" + uid);
        String assetId = executor().execute("asset.query", dm.root(), null, null).value("id");
        if (assetId == null) {
            throw new Exception("Transform definition (uid=" + uid + ") does not exist.");
        }
        return assetId;
    }

    /**
     * Private constructor.
     */
    private TransformDefinitionRepository(ServiceExecutor executor) {
        super(executor);
    }

    /**
     * Gets the instance of the transform definition repository.
     * 
     * @return
     */
    public static TransformDefinitionRepository getInstance(ServiceExecutor executor) {
        return new TransformDefinitionRepository(executor);
    }

    @Override
    protected TransformDefinition instantiate(Element ae) throws Throwable {
        String providerType = ae.value("meta/" + TransformDefinition.DOC_TYPE + "/type");
        return TransformProviderRegistry.getTransformProviderInstance(providerType, executor())
                .instantiateTransformDefinition(this, ae);
    }

    @Override
    public String uidName() {
        return TransformDefinition.UID_NAME;
    }

    /**
     * Creates a transform definition in the repository.
     * 
     * @param type
     *            the provider type of the transform definition.
     * @param name
     *            the name for the transform definition.
     * @param description
     *            the description about the transform definition.
     * @param paramDefns
     *            the parameter definitions for the transform definition.
     * @param input
     *            the content input stream for the transform definition.
     * @return
     * @throws Throwable
     */
    public TransformDefinition create(String type, String name, String description,
            List<ParameterDefinition> paramDefns, PluginService.Input input) throws Throwable {
        TransformDefinition defn = TransformProviderRegistry.getTransformProviderInstance(type, executor())
                .createTransformDefinition(this, name, description, paramDefns, input);
        return save(defn);
    }

    @Override
    protected void beforeCommit(TransformDefinition defn) throws Throwable {
        TransformProviderRegistry.getTransformProviderInstance(defn.providerType(), executor()).beforeCommit(defn);
    }

    @Override
    protected void beforeDelete(TransformDefinition defn) throws Throwable {
        TransformProviderRegistry.getTransformProviderInstance(defn.providerType(), executor()).beforeDelete(defn);
    }
}
