package transform.kepler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import transform.ParameterDefinition;
import transform.TransformDefinition;
import transform.TransformDefinitionRepository;
import transform.TransformProvider;
import transform.ParameterDefinition.DataType;
import arc.mf.plugin.PluginService;
import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;

public class KeplerTransformDefinition extends TransformDefinition {

    public static final String PARAM_TRASNFORM_UID = "__transform_uid";
    public static final String PARAM_SECURE_IDENTITY_TOKEN = "__secure_identity_token";
    public static final String PARAM_ATTR_DEFAULT = "default";
    public static final String PARAM_ATTR_PRIVATE = "private";

    protected KeplerTransformDefinition(TransformDefinitionRepository repo, Element ae) throws Throwable {
        super(repo, ae);
    }

    protected KeplerTransformDefinition(TransformDefinitionRepository repo, long uid, String name, String description,
            TransformProvider provider, List<ParameterDefinition> paramDefns, PluginService.Input input)
            throws Throwable {
        super(repo, uid, name, description, provider, paramDefns, input);
    }

    public void setParameterDefinitions(KeplerXML k) throws Throwable {
        Map<String, ParameterDefinition> map = parseParameterDefinitions(k.root(), false);
        List<ParameterDefinition> paramDefns = (map == null || map.isEmpty()) ? null
                : new ArrayList<ParameterDefinition>(map.values());
        setParameterDefinitions(paramDefns);
    }

    public static Map<String, ParameterDefinition> parseParameterDefinitions(XmlDoc.Element rootElement,
            boolean parseComposites) throws Throwable {

        Map<String, ParameterDefinition> paramDefns = new HashMap<String, ParameterDefinition>();
        List<XmlDoc.Element> elements = rootElement.elements();
        if (elements != null) {
            for (XmlDoc.Element element : elements) {
                String klass = element.value("@class");
                if (klass == null) {
                    continue;
                }
                if (element.nameEquals("property")) {
                    String name = element.value("@name");
                    String value = element.value("@value");
                    DataType dtype = DataType.STRING;
                    if (klass.equals("ptolemy.data.expr.StringParameter")) {
                        if (value != null) {
                            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                dtype = DataType.BOOLEAN;
                            } else {
                                dtype = DataType.STRING;
                            }
                        } else {
                            dtype = DataType.STRING;
                        }
                    } else if (klass.equals("ptolemy.data.expr.Parameter")) {
                        if (value != null) {
                            if (value.matches("^[+-]?\\d+$")) {
                                dtype = DataType.LONG;
                            } else if (value.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")) {
                                dtype = DataType.DOUBLE;
                            } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                dtype = DataType.BOOLEAN;
                            } else {
                                dtype = DataType.STRING;
                            }
                        } else {
                            dtype = DataType.STRING;
                        }
                    } else {
                        continue;
                    }
                    boolean hasDefault = false;
                    boolean isPrivate = false;
                    if (element.hasSubElements()) {
                        // Special case parameter attributes
                        // default will give the value
                        // private says to hide the parameter from
                        // DaRIS/Mediaflux
                        for (XmlDoc.Element subel : element.elements()) {
                            if (subel.value("@name").equalsIgnoreCase(PARAM_ATTR_DEFAULT))
                                hasDefault = true;
                            else if (subel.value("@name").equalsIgnoreCase(PARAM_ATTR_PRIVATE))
                                isPrivate = true;
                        }

                        if (name != null) {
                            // @formatter:off
	                        if (name.equals(PARAM_TRASNFORM_UID)  
	                                || name.equals(PARAM_SECURE_IDENTITY_TOKEN)
	                                || isPrivate) { //name.startsWith("_")) {
	                            // 1. bypass the transform uid parameter
	                            // 2. bypass the security token parameter
	                            // 3. bypass the private parameters (name starts with _
	                            // underscore)
	                            continue;
	                        }
	                        // @formatter:on
                            ParameterDefinition paramDefn;
                            if (hasDefault) {// (name.endsWith("_")) {
                                // optional parameter with default value. Its
                                // name
                                // ends with _ underscore.
                                paramDefn = new ParameterDefinition(name, dtype, null, 0, 1, value);
                            } else {
                                paramDefn = new ParameterDefinition(name, dtype, null, 1, 1, null);
                            }
                            paramDefns.put(name, paramDefn);
                        }
                    }
                } else if (element.nameEquals("entity")) {
                    if (klass.equals("ptolemy.actor.TypedCompositeActor")) {
                        paramDefns.putAll(parseParameterDefinitions(element, parseComposites));
                    }
                }
            }
        }
        return paramDefns;
    }
}
