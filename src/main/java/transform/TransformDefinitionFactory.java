package transform;

import java.util.List;

import arc.mf.plugin.PluginService;
import arc.xml.XmlDoc;

public interface TransformDefinitionFactory {

    /**
     * Instantiates a transform definition from the given XML element.
     * 
     * @param repo
     *            the associated transform definition repository.
     * @param ae
     *            the XML element represents the transform definition.
     * @return the transform definition from the repository.
     * @throws Throwable
     */
    TransformDefinition instantiateTransformDefinition(TransformDefinitionRepository repo, XmlDoc.Element ae)
            throws Throwable;

    /**
     * Creates a local uncommitted transform definition.
     * 
     * @param repo
     *            the associated transform definition repository (to commit to).
     * @param name
     *            the name of the transform definition.
     * @param description
     *            the description about the transform definition.
     * @param paramDefns
     *            the parameter definitions for the transform definition.
     * @param input
     *            the content input stream for the transform definition.
     * @return
     * @throws Throwable
     */
    TransformDefinition createTransformDefinition(TransformDefinitionRepository repo, String name, String description,
            List<ParameterDefinition> paramDefns, PluginService.Input input) throws Throwable;
}
