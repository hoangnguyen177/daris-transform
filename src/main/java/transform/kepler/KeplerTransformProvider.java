package transform.kepler;

import java.io.File;
import java.util.List;
import java.util.Map;

import transform.Parameter;
import transform.ParameterDefinition;
import transform.Transform;
import transform.TransformDefinition;
import transform.TransformDefinitionRepository;
import transform.TransformProvider;
import transform.TransformRepository;
import transform.mf.SecureIdentityToken;
import transform.util.StreamUtil;
import arc.dtype.IntegerType;
import arc.dtype.StringType;
import arc.dtype.XmlDocType;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.PluginService.Input;
import arc.mf.plugin.ServiceExecutor;
import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;
import arc.xml.XmlDocDefinition;

public class KeplerTransformProvider extends TransformProvider {

    public static final String TYPE = "kepler";
    public static final String USER_SPECIFIC_SETTINGS_KEPLER_SERVER = "kepler.server";

    public KeplerTransformProvider(ServiceExecutor executor) {
        super(executor);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public String description() {
        return "Kepler workflow system.";
    }

    @Override
    public TransformDefinition instantiateTransformDefinition(TransformDefinitionRepository repo, Element ae)
            throws Throwable {

        return new KeplerTransformDefinition(repo, ae);
    }

    @Override
    public TransformDefinition createTransformDefinition(TransformDefinitionRepository repo, String name,
            String description, List<ParameterDefinition> paramDefns, Input input) throws Throwable {
        return new KeplerTransformDefinition(repo, repo.uidNext(), name, description, this, paramDefns, input);
    }

    public void beforeCommit(TransformDefinition defn) throws Throwable {

        KeplerTransformDefinition td = (KeplerTransformDefinition) defn;

        if (!td.created() && td.contentInput() == null) {
            throw new IllegalArgumentException("The input stream for the Kepler transform definition is null.");
        }

        if (td.contentInput() != null) {

            td.clearParameterDefinitions();

            // save input stream into a temporary file for future multiple uses.
            File tf = PluginService.createTemporaryFile();
            StreamUtil.save(td.contentInput().stream(), tf);

            // parse kepler workflow file to get the parameter definitions
            KeplerXML kxml = new KeplerXML(tf);
            td.setParameterDefinitions(kxml);

            // Update the input (because it has been consumed.)
            PluginService.Input input = new PluginService.Input(PluginService.deleteOnCloseInputStream(tf),
                    tf.length(), kxml.sourceMimeType().name(), tf.getName() + "."
                            + kxml.sourceMimeType().defaultExtension());
            td.setContentInput(input);
        }
    }

    public void beforeDelete(Transform transform) throws Throwable {
        /*
         * destroy the secure identity token before destroy the asset.
         */
        String secureIdentityToken = transform.secureIdentityToken();
        if (secureIdentityToken != null) {
            SecureIdentityToken.destroy(executor(), secureIdentityToken);
        }
    }

    @Override
    public Transform instantiateTransform(TransformRepository repo, Element ae) throws Throwable {

        return new KeplerTransform(repo, ae);
    }

    @Override
    public Transform createTransform(TransformRepository repo, TransformDefinition defn, String name,
            String description, List<Parameter> params, Map<String, String> properties, Input input) throws Throwable {

        assert type().equals(defn.providerType());
        return new KeplerTransform(repo, defn, repo.uidNext(), name, description, params, properties, input);
    }

    @Override
    public void addUserSpecificSettingsDefinitionItems(XmlDocDefinition.Element ussde) {
        XmlDocDefinition.Element kse = new XmlDocDefinition.Element(USER_SPECIFIC_SETTINGS_KEPLER_SERVER,
                XmlDocType.DEFAULT, "Kepler server settings", 1, 1);
        kse.add(new XmlDocDefinition.Element("host", StringType.DEFAULT, "The host name of the Kepler server.", 1, 1));
        kse.add(new XmlDocDefinition.Element("port", new IntegerType(0, 65535),
                "The port number of the Kepler server.", 0, 1));

        XmlDocDefinition.Element lse = new XmlDocDefinition.Element("launcher-service", XmlDocType.DEFAULT,
                "The mediaflux service to start the remote Kepler server.", 0, 1);
        lse.add(new XmlDocDefinition.Attribute("name", StringType.DEFAULT, "The name of the launcher service.", 1));
        XmlDocDefinition.Element lsae = new XmlDocDefinition.Element("args", XmlDocType.DEFAULT,
                "The arguments for the launcher service.", 0, 1);
        lsae.setIgnoreDescendants(true);
        lse.add(lsae);
        lse.add(new XmlDocDefinition.Element("port-xpath", StringType.DEFAULT,
                "The xpath to retrieve port value from the service output.", 0, 1));
        kse.add(lse);
        ussde.add(kse);
    }

    public KeplerServerSettings keplerServerSettings() throws Throwable {

        XmlDoc.Element ksse = userSpecificSettings().element(USER_SPECIFIC_SETTINGS_KEPLER_SERVER);
        return new KeplerServerSettings(ksse);
    }

    public void setKeplerServerSettings(KeplerServerSettings serverSettings) throws Throwable {
        XmlDoc.Element usse = userSpecificSettings();
        if (usse.elementExists(USER_SPECIFIC_SETTINGS_KEPLER_SERVER)) {
            usse.remove(usse.element(USER_SPECIFIC_SETTINGS_KEPLER_SERVER));
        }
        usse.add(serverSettings.toXmlElement());
        setUserSpecificSettings(usse);
    }

}
