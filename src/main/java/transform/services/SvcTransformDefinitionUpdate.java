package transform.services;

import java.util.List;

import transform.ParameterDefinition;
import transform.TransformDefinition;
import transform.TransformDefinitionRepository;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.ServiceExecutor;
import arc.mf.plugin.atomic.AtomicOperation;
import arc.mf.plugin.atomic.AtomicTransaction;
import arc.mf.plugin.dtype.LongType;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlWriter;

public class SvcTransformDefinitionUpdate extends PluginService {

    public static final String SERVICE_NAME = "transform.definition.update";

    private Interface _defn;

    public SvcTransformDefinitionUpdate() throws Throwable {
        _defn = new Interface();
        _defn.add(new Interface.Element("uid", LongType.POSITIVE_ONE, "the unique id of the transform definition.", 1,
                1));
        SvcTransformDefinitionCreate.addToInterface(_defn);
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
        return "Updates a transform definition.";
    }

    @Override
    public int maxNumberOfInputs() {
        return 1;
    }

    @Override
    public int minNumberOfInputs() {
        return 0;
    }

    @Override
    public void execute(final Element args, final Inputs inputs, Outputs outputs, XmlWriter w) throws Throwable {
        new AtomicTransaction(new AtomicOperation() {
            @Override
            public boolean execute(ServiceExecutor executor) throws Throwable {
                long uid = args.longValue("uid");
                String name = args.value("name");
                String description = args.value("description");
                List<ParameterDefinition> paramDefns = ParameterDefinition.instantiateList(args.elements("parameter"));
                TransformDefinition td = TransformDefinitionRepository.getInstance(executor).get(uid);
                td.setName(name);
                td.setDescription(description);
                td.setParameterDefinitions(paramDefns);
                td.setContentInput(inputs == null ? null : inputs.input(0));
                td.commitChanges();
                return false;
            }
        }).execute(executor());

    }

    @Override
    public String name() {
        return SERVICE_NAME;
    }

}
