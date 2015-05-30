package transform;

import arc.mf.plugin.PluginThread;
import arc.mf.plugin.ServiceExecutor;
import arc.xml.XmlDocMaker;

/**
 * A singleton class contains methods to manage application properties.
 * 
 * @author wilson
 * 
 */
public class Application {

    public static final String NAME = "transform";
    public static final String NAMESPACE = "transform";

    /**
     * Sets the application property with the given property name and value. It
     * calls the application.property.create and application.property.set
     * services using the given service executor.
     * 
     * @param executor
     *            the service executor.
     * @param name
     *            the name of the property.
     * @param value
     *            the value of the property.
     * @throws Throwable
     */
    public static void setProperty(ServiceExecutor executor, String name, String value) throws Throwable {
        // create the property
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("ifexists", "ignore");
        dm.push("property", new String[] { "app", Application.NAME, "name", name });
        executor.execute("application.property.create", dm.root());

        // set the property value
        dm = new XmlDocMaker("args");
        dm.add("property", new String[] { "app", Application.NAME, "name", name }, value);
        executor.execute("application.property.set", dm.root());
    }

    /**
     * Sets the application property with the given property name and value. It
     * calls the application.property.create and application.property.set
     * services using PluginThread.serviceExecutor().
     * 
     * @param name
     *            the name of the property.
     * @param value
     *            the value of the property.
     * @throws Throwable
     */
    public static void setProperty(String name, String value) throws Throwable {
        setProperty(PluginThread.serviceExecutor(), name, value);
    }

    /**
     * Gets the application property with given property name.
     * 
     * @param executor
     *            the service executor.
     * @param name
     *            the name of the property.
     * @return the value of the property.
     * @throws Throwable
     */
    public static String getProperty(ServiceExecutor executor, String name) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("app", Application.NAME);
        dm.add("name", name);
        return executor.execute("application.property.describe", dm.root()).value("property/value");
    }

    /**
     * Gets the application property with given property name.
     * 
     * @param name
     *            the name of the property.
     * @return the value of the property.
     * @throws Throwable
     */
    public static String getProperty(String name) throws Throwable {
        return getProperty(PluginThread.serviceExecutor(), name);
    }

    /**
     * Reset/remove the property with the given name.
     * 
     * @param executor
     *            the service executor.
     * @param name
     *            the name of the application property.
     * @throws Throwable
     */
    public static void resetProperty(ServiceExecutor executor, String name) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("property", new String[] { "app", Application.NAME, "name", name });
        executor.execute("application.property.destroy", dm.root());
    }

    /**
     * Reset/remove the property with the given name.
     * 
     * @param name
     *            the name of the application property.
     * @throws Throwable
     */
    public static void resetProperty(String name) throws Throwable {
        resetProperty(PluginThread.serviceExecutor(), name);
    }

    /**
     * Checks if the property with given name exists.
     * 
     * @param executor
     *            the service executor.
     * @param name
     *            the name of the property.
     * @return true if the property exists.
     * @throws Throwable
     */
    public static boolean propertyExists(ServiceExecutor executor, String name) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("property", new String[] { "app", Application.NAME }, name);
        return executor.execute("application.property.exists", dm.root()).booleanValue("exists");
    }

    /**
     * Checks if the property with given name exists.
     * 
     * @param name
     *            the name of the property.
     * @return true if the property exists.
     * @throws Throwable
     */
    public static boolean propertyExists(String name) throws Throwable {
        return propertyExists(PluginThread.serviceExecutor(), name);
    }

}
