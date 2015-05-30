package transform;

import arc.dtype.XmlDocType;
import arc.mf.plugin.ServiceExecutor;
import arc.xml.XmlDoc;
import arc.xml.XmlDocDefinition;
import arc.xml.XmlDocDefinition.Element;

/**
 * Transform provider class is to be extended for specific external transform
 * providers, e.g. Kepler workflow system.
 * 
 * @author Wei Liu (wliu1976@gmail.com)
 * 
 */
public abstract class TransformProvider implements TransformDefinitionFactory, TransformFactory {

    private ServiceExecutor _executor;

    /**
     * The constructor.
     * 
     * @param executor
     */
    protected TransformProvider(ServiceExecutor executor) {
        _executor = executor;
    }

    /**
     * The associated Mediaflux service executor.
     * 
     * @return
     */
    protected ServiceExecutor executor() {
        return _executor;
    }

    /**
     * 
     * @return
     */
    protected TransformRepository transformRepository() {
        return TransformRepository.getInstance(executor());
    }

    /**
     * 
     * @return
     */
    protected TransformDefinitionRepository transformDefinitionRepository() {
        return TransformDefinitionRepository.getInstance(executor());
    }

    /**
     * The type of the transform provider. It is the key to identify the
     * transform provider.
     * 
     * @return
     */
    public abstract String type();

    /**
     * The description about the transform provider.
     * 
     * @return
     */
    public abstract String description();

    /**
     * The name prefix for the application property.
     * 
     * @return
     */
    private String propertyNamePrefix() {
        return "transform.provider." + type() + ".";
    }

    /**
     * Sets the property with the given name and value.
     * 
     * @param name
     *            the name of the property.
     * @param value
     *            the value of the property.
     * @throws Throwable
     */
    public void setProperty(String name, String value) throws Throwable {

        Application.setProperty(executor(), propertyNamePrefix() + name, value);
    }

    /**
     * Gets the value of the property with given name.
     * 
     * @param name
     * @return
     * @throws Throwable
     */
    public String getProperty(String name) throws Throwable {

        return Application.getProperty(executor(), propertyNamePrefix() + name);
    }

    /**
     * Returns the definition of the user specific settings for the transform
     * provider.
     * 
     * @return
     */
    public XmlDocDefinition.Element userSpecificSettingsDefinition() {
        XmlDocDefinition.Element ussde = new XmlDocDefinition.Element(userSpecificSettingsRootName(),
                XmlDocType.DEFAULT, null, 1, 1);
        addUserSpecificSettingsDefinitionItems(ussde);
        return ussde;
    }

    protected abstract void addUserSpecificSettingsDefinitionItems(Element ussde);

    /**
     * The root element name of the user settings for the transform provider.
     * 
     * @return
     */
    public final String userSpecificSettingsRootName() {
        return "transform.provider." + type();
    }

    /**
     * Gets the user specific settings of the current user.
     * 
     * @return
     * @throws Throwable
     */
    public XmlDoc.Element userSpecificSettings() throws Throwable {
        XmlDoc.Element use = UserSelfSettings.get(executor());
        if (use != null) {
            XmlDoc.Element usse = use.element(userSpecificSettingsRootName());
            if (usse != null) {
                return usse;
            }
        }
        return new XmlDoc.Element(userSpecificSettingsRootName());
    }

    /**
     * Gets the user specific settings of the given user.
     * 
     * @param domain
     *            the domain of the user
     * @param user
     *            the name of the user
     * @return
     * @throws Throwable
     */
    public XmlDoc.Element userSpecificSettings(String domain, String user) throws Throwable {
        XmlDoc.Element use = UserSettings.get(executor(), domain, user);
        if (use != null) {
            XmlDoc.Element usse = use.element(userSpecificSettingsRootName());
            if (usse != null) {
                return usse;
            }
        }
        return new XmlDoc.Element(userSpecificSettingsRootName());
    }

    /**
     * Sets the user specific settings for the user self.
     * 
     * @param se
     *            the XML element represents the user settings.
     * @throws Throwable
     */
    public void setUserSpecificSettings(XmlDoc.Element usse) throws Throwable {

        if (usse != null) {
            assert usse.nameEquals(userSpecificSettingsRootName());
        }
        XmlDocDefinition.Element ussde = userSpecificSettingsDefinition();
        if (ussde != null && usse != null) {
            ussde.validate("Validating user specific settings.", usse, true, true);
        }

        XmlDoc.Element se = UserSelfSettings.get(executor());
        if (se == null) {
            se = new XmlDoc.Element("settings");
        }
        if (usse != null && se.elementExists(usse.name())) {
            se.remove(se.element(usse.name()));
        }
        se.add(usse);
        UserSelfSettings.set(executor(), se);
    }

    /**
     * Sets the user specific settings for the given user.
     * 
     * @param domain
     *            the authentication domain.
     * @param user
     *            the name of the user.
     * @param usse
     *            the settings.
     * @throws Throwable
     */
    public void setUserSpecificSettings(String domain, String user, XmlDoc.Element usse) throws Throwable {
        if (usse != null) {
            assert usse.nameEquals(userSpecificSettingsRootName());
        }
        XmlDocDefinition.Element ussde = userSpecificSettingsDefinition();
        if (ussde != null && usse != null) {
            ussde.validate("Validating user specific settings.", usse, true, true);
        }

        XmlDoc.Element se = UserSettings.get(executor(), domain, user);
        if (se == null) {
            se = new XmlDoc.Element("settings");
        }
        if (usse != null && se.elementExists(usse.name())) {
            se.remove(se.element(usse.name()));
        }
        se.add(usse);
        UserSettings.set(executor(), domain, user, se);
    }

    /**
     * Action to take before committing the changes on the given transform
     * definition.
     * 
     * @param td
     * @throws Throwable
     */
    protected void beforeCommit(TransformDefinition td) throws Throwable {

    }

    /**
     * Action to take before committing the changes on the given transform.
     * 
     * @param t
     * @throws Throwable
     */
    protected void beforeCommit(Transform t) throws Throwable {

    }

    /**
     * Action to take before destroying on the given transform definition.
     * 
     * @param td
     * @throws Throwable
     */
    protected void beforeDelete(TransformDefinition td) throws Throwable {

    }

    /**
     * Action to take before destroying the given transform.
     * 
     * @param t
     * @throws Throwable
     */
    protected void beforeDelete(Transform t) throws Throwable {

    }

}
