package transform.exec;

import java.util.List;

import transform.ParameterDefinition;
import transform.TransformDefinition;
import transform.TransformDefinitionRepository;
import transform.TransformProvider;
import arc.mf.plugin.PluginService;
import arc.xml.XmlDoc.Element;

public class ExecTransformDefinition extends TransformDefinition {

    public ExecTransformDefinition(TransformDefinitionRepository repo, Element ae) throws Throwable {
        super(repo, ae);
    }

    public ExecTransformDefinition(TransformDefinitionRepository repo, long uid, String name, String description,
            TransformProvider provider, List<ParameterDefinition> paramDefns, PluginService.Input input)
            throws Throwable {
        super(repo, uid, name, description, provider, paramDefns, input);
    }

//    @Override
//    protected void beforeCreateObject(Arguments args) throws Throwable {
//
//        TypedInput input = args.input();
//        if (input != null) {
//
//            // save input stream into a temporary file for future multiple uses.
//            File tf = PluginService.createTemporaryFile();
//            StreamUtil.save(input.toServiceInput().stream(), tf);
//
//            // Update the input (because it has been consumed.)
//            args.setInput(new TypedInput(PluginService.deleteOnCloseInputStream(tf), tf.length(), "plain/text", tf
//                    .getName() + ".sh"));
//        }
//    }
//
//    @Override
//    protected void beforeCommitChanges(TypedInput input) throws Throwable {
//
//        if (input != null) {
//            // save input stream into a temporary file for future multiple uses.
//            File tf = PluginService.createTemporaryFile();
//            StreamUtil.save(input.toServiceInput().stream(), tf);
//
//            // Update the input (because it has been consumed.)
//            input.set(PluginService.deleteOnCloseInputStream(tf), tf.length(), "plain/text", tf.getName() + ".sh");
//        }
//    }
//
//    @Override
//    public void validate() throws Throwable {
//
//        super.validate();
//
//        Map<String, TransformDefinition.ParameterDefinition> pds = paramDefinitions();
//        if ((pds == null || pds.isEmpty() || (!pds.containsKey(ExecTransform.PARAM_COMMAND)) && !hasContent())) {
//            throw new Exception("Invalid exec transform definition: " + uid() + ". Neither parameter : "
//                    + ExecTransform.PARAM_COMMAND + " nor content script is found.");
//        }
//        if ((pds != null && !pds.isEmpty() && (pds.containsKey(ExecTransform.PARAM_COMMAND)) && hasContent())) {
//            throw new Exception("Invalid exec transform definition: " + uid() + ". Both parameter : "
//                    + ExecTransform.PARAM_COMMAND + " and content script are found.");
//        }
//    }
//
//    @Override
//    public void validate(TransformDefinition.Arguments args) throws Throwable {
//
//        super.validate(args);
//
//        Map<String, TransformDefinition.ParameterDefinition> pds = args.paramDefns();
//        if ((pds == null || pds.isEmpty() || (!pds.containsKey(ExecTransform.PARAM_COMMAND)) && args.input() == null)) {
//            throw new Exception("Invalid exec transform definition: " + uid() + ". Neither parameter : "
//                    + ExecTransform.PARAM_COMMAND + " nor content script is found.");
//        }
//        if ((pds != null && !pds.isEmpty() && (pds.containsKey(ExecTransform.PARAM_COMMAND)) && args.input() != null)) {
//            throw new Exception("Invalid exec transform definition: " + uid() + ". Both parameter : "
//                    + ExecTransform.PARAM_COMMAND + " and content script are found.");
//        }
//    }

}
