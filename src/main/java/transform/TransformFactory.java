package transform;

import java.util.List;
import java.util.Map;

import arc.mf.plugin.PluginService;
import arc.xml.XmlDoc;

public interface TransformFactory {

    /**
     * Instantiates a transform from the given XML element.
     * 
     * @param repo
     *            the associated transform repository
     * @param ae
     *            the XML element represents the transform.
     * @return the transform from the repository.
     * @throws Throwable
     */
    Transform instantiateTransform(TransformRepository repo, XmlDoc.Element ae) throws Throwable;

    /**
     * Creates a local uncommitted transform.
     * 
     * @param repo
     *            the associated transform repository.
     * @param defn
     *            the transform definition.
     * @param name
     *            the name of the transform.
     * @param description
     *            the description about the transform.
     * @param params
     *            the parameters for the transform.
     * @param properties
     *            the runtime properties for the transform.
     * @param input
     *            the content input stream for the transform.
     * @return the local uncommitted transform.
     * @throws Throwable
     */
    Transform createTransform(TransformRepository repo, TransformDefinition defn, String name, String description,
            List<Parameter> params, Map<String, String> properties, PluginService.Input input) throws Throwable;

}
