package transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import transform.util.ObjectUtil;
import arc.xml.XmlDoc;
import arc.xml.XmlWriter;

/**
 * The parameter for the transform.
 * 
 * @author wilson
 * 
 */
public class Parameter {
    private String _name;
    private String _value;

    /**
     * Constructor.
     * 
     * @param name
     *            the name of the parameter.
     * @param value
     *            the value of the parameter.
     */
    public Parameter(String name, String value) {
        _name = name;
        _value = value;
    }

    /**
     * Constructor.
     * 
     * @param pe
     *            the XML element represents the paramter.
     * @throws Throwable
     */
    public Parameter(XmlDoc.Element pe) throws Throwable {
        this(pe.value("@name"), pe.value());
    }

    /**
     * The name of the parameter.
     * 
     * @return
     */
    public String name() {
        return _name;
    }

    /**
     * The value of the parameter.
     * 
     * @return
     */
    public String value() {
        return _value;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null) {
            if (o instanceof Parameter) {
                return (_name.equals(((Parameter) o).name()) && ObjectUtil.equals(_value, ((Parameter) o).value()));
            }
        }
        return false;
    }

    /**
     * Saves the object into a XML writer.
     * 
     * @param w
     *            the XML writer.
     * @throws Throwable
     */
    public void save(XmlWriter w) throws Throwable {
        w.add("parameter", new String[] { "name", name() }, value());
    }

    /**
     * Instantiates the parameters from a list of XML elements.
     * 
     * @param pes
     *            the list of XML elements represents the parameters.
     * @return
     * @throws Throwable
     */
    public static Map<String, Parameter> instantiateMap(List<XmlDoc.Element> pes) throws Throwable {
        if (pes != null) {
            Map<String, Parameter> params = new HashMap<String, Parameter>();
            for (XmlDoc.Element pe : pes) {
                Parameter p = new Parameter(pe);
                params.put(p.name(), p);
            }
            if (!params.isEmpty()) {
                return params;
            }
        }
        return null;
    }

    /**
     * Instantiates the parameters from a list of XML elements.
     * 
     * @param pes
     *            the list of XML elements represents the parameters.
     * @return
     * @throws Throwable
     */
    public static List<Parameter> instantiateList(List<XmlDoc.Element> pes) throws Throwable {
        if (pes != null && !pes.isEmpty()) {
            List<Parameter> params = new ArrayList<Parameter>();
            for (XmlDoc.Element pe : pes) {
                Parameter p = new Parameter(pe);
                params.add(p);
            }
            if (!params.isEmpty()) {
                return params;
            }
        }
        return null;
    }
}
