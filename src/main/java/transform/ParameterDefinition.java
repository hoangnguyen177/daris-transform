package transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import transform.util.ObjectUtil;
import arc.xml.XmlDoc;
import arc.xml.XmlWriter;

/**
 * The parameter definition class.
 * 
 * @author wilson
 * 
 */
public class ParameterDefinition {
    /**
     * Enums for the data types.
     * 
     */
    public static enum DataType {

        STRING, BOOLEAN, INTEGER, LONG, FLOAT, DOUBLE;

        @Override
        public String toString() {

            return super.toString().toLowerCase();
        }

        public static DataType fromString(String type) throws Throwable {
            return valueOf(type.toUpperCase());
        }

    }

    private String _name;
    private DataType _type;
    private String _description;
    private int _minOccurs;
    private int _maxOccurs;
    private String _value;

    /**
     * Constructor.
     * 
     * @param name
     *            the name of parameter.
     * @param type
     *            the data type of the parameter.
     * @param description
     *            description about the parameter if any.
     * @param minOccurs
     *            minimum occurrences of the parameter.
     * @param maxOccurs
     *            maximum occurrences of the parameter.
     * @param value
     */
    public ParameterDefinition(String name, DataType type, String description, int minOccurs, int maxOccurs,
            String value) {
        _name = name;
        _type = type;
        _description = description;
        _minOccurs = minOccurs;
        _maxOccurs = maxOccurs;
        _value = value;
    }

    /**
     * Constructor.
     * 
     * @param pde
     *            the XML element represents the parameter definition.
     * @throws Throwable
     */
    public ParameterDefinition(XmlDoc.Element pde) throws Throwable {

        _name = pde.value("@name");
        _type = DataType.fromString(pde.value("@type"));
        _description = pde.value("description");
        _minOccurs = pde.intValue("@min-occurs", 1);
        _maxOccurs = pde.intValue("@max-occurs", 1);
        _value = pde.value("value");
    }

    /**
     * The minimum number of occurrences required.
     * 
     * @return
     */
    public int minOccurs() {
        return _minOccurs;
    }

    /**
     * The maximum number of occurrences required.
     * 
     * @return
     */
    public int maxOccurs() {
        return _maxOccurs;
    }

    /**
     * The name of the parameter
     * 
     * @return
     */
    public String name() {
        return _name;
    }

    /**
     * The description about the parameter.
     * 
     * @return
     */
    public String description() {
        return _description;
    }

    /**
     * The (pre-specified) value of the parameter if applicable.
     * 
     * @return
     */
    public String value() {
        return _value;
    }

    /**
     * The data type of the parameter.
     * 
     * @return
     */
    public DataType type() {
        return _type;
    }

    /**
     * Save the parameter definition as XML.
     * 
     * @param w
     * @throws Throwable
     */
    public void save(XmlWriter w) throws Throwable {
        w.push("parameter",
                new String[] { "type", type().toString(), "name", name(), "min-occurs", Integer.toString(minOccurs()),
                        "max-occurs", Integer.toString(maxOccurs()) });
        if (description() != null) {
            w.add("description", description());
        }
        if (value() != null) {
            w.add("value", value());
        }
        w.pop();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null) {
            if (o instanceof ParameterDefinition) {
                return ObjectUtil.equals(((ParameterDefinition) o).name(), name());
            }
        }
        return false;
    }

    /**
     * Instantiates the parameter definitions from the given list of XML
     * elements.
     * 
     * @param pdes
     *            the list of XML elements represent the parameter definitions.
     * @return
     * @throws Throwable
     */
    public static List<ParameterDefinition> instantiateList(List<XmlDoc.Element> pdes) throws Throwable {
        if (pdes == null || pdes.isEmpty()) {
            return null;
        }
        List<ParameterDefinition> params = new ArrayList<ParameterDefinition>(pdes.size());
        for (XmlDoc.Element pde : pdes) {
            params.add(new ParameterDefinition(pde));
        }
        return params;
    }

    /**
     * Instantiates the parameter definitions from the given list of XML
     * elements.
     * 
     * @param pdes
     *            the list of XML elements represent the parameter definitions.
     * @return
     * @throws Throwable
     */
    public static Map<String, ParameterDefinition> instantiateMap(List<XmlDoc.Element> pdes) throws Throwable {
        if (pdes == null || pdes.isEmpty()) {
            return null;
        }
        Map<String, ParameterDefinition> params = new HashMap<String, ParameterDefinition>(pdes.size());
        for (XmlDoc.Element pde : pdes) {
            ParameterDefinition pd = new ParameterDefinition(pde);
            params.put(pd.name(), pd);
        }
        return params;
    }

}
