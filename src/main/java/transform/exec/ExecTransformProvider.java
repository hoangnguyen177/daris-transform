package transform.exec;

import java.util.List;
import java.util.Map;

import transform.Parameter;
import transform.ParameterDefinition;
import transform.Transform;
import transform.TransformDefinition;
import transform.TransformDefinitionRepository;
import transform.TransformProvider;
import transform.TransformRepository;
import arc.mf.plugin.PluginService.Input;
import arc.mf.plugin.ServiceExecutor;
import arc.xml.XmlDoc.Element;

public class ExecTransformProvider extends TransformProvider {

    public static final String TYPE = "exec";

    public ExecTransformProvider(ServiceExecutor executor) {
        super(executor);
    }

    @Override
    public TransformDefinition instantiateTransformDefinition(TransformDefinitionRepository repo, Element ae)
            throws Throwable {
        return new ExecTransformDefinition(repo, ae);
    }

    @Override
    public TransformDefinition createTransformDefinition(TransformDefinitionRepository repo, String name,
            String description, List<ParameterDefinition> paramDefns, Input input) throws Throwable {
        return new ExecTransformDefinition(repo, repo.uidNext(), name, description, this, paramDefns, input);
    }

    @Override
    public Transform instantiateTransform(TransformRepository repo, Element ae) throws Throwable {
        return new ExecTransform(repo, ae);
    }

    @Override
    public Transform createTransform(TransformRepository repo, TransformDefinition defn, String name,
            String description, List<Parameter> params, Map<String, String> properties, Input input) throws Throwable {
        return new ExecTransform(repo, defn, repo.uidNext(), name, description, params, properties, input);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public String description() {
        return "The provider executes the local system commands.";
    }

    @Override
    protected void addUserSpecificSettingsDefinitionItems(arc.xml.XmlDocDefinition.Element ussde) {
        // not user specific settings defined.

    }

}
