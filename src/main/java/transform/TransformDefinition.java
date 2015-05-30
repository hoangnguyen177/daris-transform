package transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import transform.mf.ACL;
import transform.mf.User;
import transform.mf.ACL.Access;
import arc.mf.plugin.PluginService;
import arc.xml.XmlDoc;
import arc.xml.XmlDocMaker;
import arc.xml.XmlWriter;

/**
 * The transform definition.
 * 
 * @author Wei Liu (wliu1976@gmail.com)
 * 
 */
public abstract class TransformDefinition extends Entity {

    public static final String UID_NAME = "transform:transform-definition";
    public static final String DOC_TYPE = "transform:transform-definition";
    public static final String NAMESPACE = Application.NAMESPACE + "/definition";

    private TransformDefinitionRepository _repo;

    private Map<String, ParameterDefinition> _paramDefns;

    /**
     * Constructor.
     * 
     * @param ae
     *            the XML element represents the transform definition asset. It
     *            must be the result asset element returned by asset.get or
     *            asset.query service with get-meta action.
     * @throws Throwable
     */
    protected TransformDefinition(TransformDefinitionRepository repo, XmlDoc.Element ae) throws Throwable {
        super(ae);
        _repo = repo;
        XmlDoc.Element tde = ae.element("meta/" + docType());
        _paramDefns = ParameterDefinition.instantiateMap(tde.elements("parameter"));
    }

    protected TransformDefinition(TransformDefinitionRepository repo, long uid, String name, String description,
            TransformProvider provider, List<ParameterDefinition> paramDefns, PluginService.Input input)
            throws Throwable {
        super(uid, name, description, provider, TransformDefinition.NAMESPACE, input, defaultAcls());
        _repo = repo;
        if (paramDefns != null && !paramDefns.isEmpty()) {
            _paramDefns = new HashMap<String, ParameterDefinition>();
            for (ParameterDefinition paramDefn : paramDefns) {
                _paramDefns.put(paramDefn.name(), paramDefn);
            }
        }
    }

    @Override
    public final String docType() {
        return TransformDefinition.DOC_TYPE;
    }

    /**
     * The transform provider.
     * 
     * @return
     * @throws Throwable
     */
    public TransformProvider provider() throws Throwable {
        return TransformProviderRegistry.getTransformProviderInstance(providerType(), executor());
    }

    /**
     * The parameter definitions for the transform.
     * 
     * @return
     */
    public List<ParameterDefinition> parameterDefinitions() {
        if (_paramDefns != null && !_paramDefns.isEmpty()) {
            return new ArrayList<ParameterDefinition>(_paramDefns.values());
        }
        return null;
    }

    /**
     * Sets the parameter definitions.
     * 
     * @param paramDefns
     */
    public void setParameterDefinitions(List<ParameterDefinition> paramDefns) {
        if (_paramDefns == null) {
            _paramDefns = new HashMap<String, ParameterDefinition>();
        } else {
            _paramDefns.clear();
        }
        if (paramDefns != null) {
            for (ParameterDefinition pd : paramDefns) {
                addParameterDefinition(pd);
            }
        }
    }

    /**
     * Removes all parameter definitions.
     */
    public void clearParameterDefinitions() {
        if (_paramDefns != null) {
            _paramDefns.clear();
        }
    }

    /**
     * Gets the parameter definition for the given name.
     * 
     * @param name
     *            the name of the parameter.
     * @return
     */
    public ParameterDefinition parameterDefinition(String name) {
        if (_paramDefns == null) {
            return null;
        }
        return _paramDefns.get(name);
    }

    /**
     * Adds a parameter definition.
     * 
     * @param paramDefn
     *            the transform parameter definition.
     */
    public void addParameterDefinition(ParameterDefinition paramDefn) {
        if (_paramDefns == null) {
            _paramDefns = new HashMap<String, ParameterDefinition>();
        }
        _paramDefns.put(paramDefn.name(), paramDefn);
    }

    @Override
    public void save(XmlWriter w, boolean doc) throws Throwable {
        if (doc) {
            w.push(docType());
        }
        w.add("uid", uid());
        w.add("type", providerType());
        if (name() != null) {
            w.add("name", name());
        }
        if (description() != null) {
            w.add("description", description());
        }
        if (_paramDefns != null && !_paramDefns.isEmpty()) {
            for (ParameterDefinition p : _paramDefns.values()) {
                p.save(w);
            }
        }
        if (doc) {
            w.pop();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected TransformDefinitionRepository repository() {
        return _repo;
    }

    @Override
    public boolean hasDependants() throws Throwable {
        return numberOfInstances() > 0;
    }

    public int numberOfInstances() throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("action", "count");
        dm.add("where", "xpath(" + Transform.DOC_TYPE + "/definition)=" + uid());
        return executor().execute("asset.query", dm.root()).intValue("value", 0);
    }

    public void valiateParameters(List<Parameter> parameters) throws Throwable {

        /*
         * put the parameters in a map.
         */
        Map<String, Parameter> params = new HashMap<String, Parameter>();
        if (parameters != null) {
            for (Parameter p : parameters) {
                params.put(p.name(), p);
            }
        }

        Map<String, ParameterDefinition> paramDefns = _paramDefns == null ? new HashMap<String, ParameterDefinition>()
                : _paramDefns;

        for (ParameterDefinition pd : paramDefns.values()) {
            Parameter p = params.get(pd.name());
            if (p == null && pd.value() != null) {
                params.put(pd.name(), new Parameter(pd.name(), pd.value()));
            }
        }

        // check if defined parameters are supplied.
        for (String pn : paramDefns.keySet()) {
            ParameterDefinition paramDefn = paramDefns.get(pn);
            Parameter param = params.get(pn);
            if (paramDefn.minOccurs() > 0 && paramDefn.value() == null && param == null) {
                throw new Exception("Required paramter: " + pn
                        + " is not found. Run service: 'transform.definition.describe :uid " + uid()
                        + "' to see the definitions of the parameters.");
            }
        }

        // check if supplied parameters are defined.
        for (String pn : params.keySet()) {
            if (paramDefns.get(pn) == null) {
                throw new Exception("Unexpected parameter: " + pn + " is found.");
            }
        }

    }

    public static List<ACL> defaultAcls() throws Throwable {
        List<ACL> acls = new ArrayList<ACL>();
        acls.add(new ACL(User.self(), Access.READ_WRITE));
        acls.add(new ACL(Roles.ADMIN_ROLE, Access.READ_WRITE));
        acls.add(new ACL(Roles.DEVELOPER_ROLE, Access.READ));
        acls.add(new ACL(Roles.USER_ROLE, Access.READ));
        return acls;
    }
}
