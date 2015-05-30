package transform.kepler;


import java.util.List;
import java.util.Map;

import transform.Parameter;
import transform.ParameterDefinition;
import transform.Transform;
import transform.Transform.Log.EventType;
import transform.TransformDefinition;
import transform.TransformRepository;
import transform.kepler.KeplerServerSettings.LauncherService;
import arc.mf.plugin.PluginService;
import arc.xml.XmlDoc;
import arc.xml.XmlDoc.Element;

public class KeplerTransform extends Transform {

    public static final String PROPERTY_KEPLER_SERVER_HOST = "kepler.server.host";

    public static final String PROPERTY_KEPLER_SERVER_PORT = "kepler.server.port";

    protected KeplerTransform(TransformRepository repo, Element ae) throws Throwable {
        super(repo, ae);
    }

    protected KeplerTransform(TransformRepository repo, TransformDefinition defn, long uid, String name,
            String description, List<Parameter> params, Map<String, String> properties, PluginService.Input input)
            throws Throwable {
        super(repo, defn, uid, name, description, params, properties, input);
    }

    protected String keplerServerHost() {

        return runtimeProperty(PROPERTY_KEPLER_SERVER_HOST);
    }

    protected void setKeplerServerHost(String host) {

        setRuntimeProperty(PROPERTY_KEPLER_SERVER_HOST, host);
    }

    protected int keplerServerPort() {

        String port = runtimeProperty(PROPERTY_KEPLER_SERVER_PORT);
        if (port == null) {
            return -1;
        }
        return Integer.parseInt(port);
    }

    protected void setKeplerServerPort(int port) {

        setRuntimeProperty(PROPERTY_KEPLER_SERVER_PORT, Integer.toString(port));
    }

    protected KeplerClient keplerClient() throws Throwable {

        String host = keplerServerHost();
        int port = keplerServerPort();
        if (host == null && port < 0) {
            throw new Exception("Kepler server (host and port) is not set.");
        }
        if (host != null) {
            return new KeplerClient(host, port, uid());
        } else {
            return new KeplerClient(port, uid());
        }
    }

    @Override
    public void execute() throws Throwable {
        /*
         * load kepler server settings
         */
        KeplerServerSettings kss = ((KeplerTransformProvider) provider()).keplerServerSettings();

        // check if host is set
        if (kss.host() == null) {
            throw new Exception("No Keper server host is set.");
        }
        setKeplerServerHost(kss.host());

        // check if port is set
        if (kss.port() > 0) {
            // if port is set, use it
            setKeplerServerPort(kss.port());
        } else {
            // port is not set, if launcher service is set
            if (kss.launcherService() == null) {
                // both port and launcher service are not set, exception
                throw new Exception(
                        "No Kepler server port or launcher service is set. Run transform.kepler.server.settings.set to set it.");
            } else {
                // use the launcher service start the remote kepler server and
                // get the server port
                LauncherService ls = kss.launcherService();
                int port = executor().execute(ls.name(), ls.args()).intValue(ls.portXPath());
                setKeplerServerPort(port);
            }
        }
        // TODO: debug
        commitChanges();

        KeplerTransformDefinition defn = (KeplerTransformDefinition) definition();

        // update the kepler definition with new parameter values
        KeplerXML kxml = new KeplerXML(defn.contentOutput().stream());
        XmlDoc.Element de = kxml.root();
        /*
         * 1. fill in transform uid (tuid) parameter if it is defined in the
         * Kepler workflow.
         */
        if (de.elementExists("/property[@name='" + KeplerTransformDefinition.PARAM_TRASNFORM_UID + "']")) {
            de.element("/property[@name='" + KeplerTransformDefinition.PARAM_TRASNFORM_UID + "']").attribute("value")
                    .setValue(uid());
        }
        /*
         * 2. fill in secure identity token (__mf_secure_identity_token__) if it
         * is defined in the Kepler workflow.
         */
        String token = secureIdentityToken();
        if (token != null
                && de.elementExists("/property[@name='" + KeplerTransformDefinition.PARAM_SECURE_IDENTITY_TOKEN + "']")) {
            de.element("/property[@name='" + KeplerTransformDefinition.PARAM_SECURE_IDENTITY_TOKEN + "']")
                    .attribute("value").setValue(token);
        }

        if (parameters() != null) {
            for (Parameter param : parameters()) {
                ParameterDefinition paramDefn = defn.parameterDefinition(param.name());
                String paramName = param.name();
                if (paramDefn.minOccurs() == 0) {
                    // optional parameter, append trailing _
                    paramName = param.name();
                    if (param.value() == null || param.value().trim().equals("")) {
                        // no value is set
                        continue;
                    }
                }
//                System.out.println("ParamName: " + paramName);
                String xpath = "/property[@name='" + paramName + "']";
                XmlDoc.Element pe = de.element(xpath);
                if (pe != null) {
                    String oldValue = pe.value("@value");
                    String newValue = param.value();
                    if (newValue != null && !newValue.equals(oldValue)) {
                        XmlDoc.Attribute attr = pe.attribute("value");
                        if (attr != null) {
                            attr.setValue(newValue);
                        } else {
                            throw new Exception("Parameter " + param.name()
                                    + " does not exist in the original Kepler XML definition.");
                        }
                    }
                }
            }
        }

        String keplerXml = KeplerXML.DTD + kxml.root().toString();

        // @formatter:off
        // debugging: save the workflow instance into volatile/tmp/
//        File tf = PluginTask.createTemporaryFile("transform_" + this.uid() + ".xml");
//        PrintStream ps = null;
//        try {
//            ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(tf)));
//            ps.print(keplerXml);
//        } finally {
//            ps.close();
//        }
        // @formatter:on

        if (!keplerClient().start(keplerXml)) {
            throw new Exception("KeplerTransform: Start failed");
        }
    }

    @Override
    public void suspend() throws Throwable {

        boolean ok = keplerClient().suspend();
        if (!ok) {
            throw new Exception("Failed to suspend transform " + uid() + ".");
        }
    }

    @Override
    public void resume() throws Throwable {

        boolean ok = keplerClient().resume();
        if (!ok) {
            throw new Exception("Failed to resume transform " + uid() + ".");
        }
    }

    @Override
    public void terminate() throws Throwable {
    	this.progress();
        keplerClient().terminate();

        // We still want to terminate the state of the workflow because the only reason
        // above did not succeed is if the kepler server is not running anymore anyway
        if (status().state() != Status.State.TERMINATED) {
        	setStatus(Status.State.TERMINATED);
        	commitChanges();
        	destroyToken();
        }
    }

    @Override
    public Progress progress() {
    	int value = -1;
    	try {
			value = keplerClient().progress();
			if (value == 100) {
				this.updateStatus();
			} else {
		    	if (super.progress() == null || (super.progress().progress() < value)) {
		    		this.setProgress(value, 100);
		    		commitChanges();
		    	}
			}
    	} catch (Throwable e) {
			e.printStackTrace();
		}
    	return super.progress();
    }    
    
    @Override
    public void updateStatus() throws Throwable {
        Status.State state = null;
        try {
            state = keplerClient().status();
        } catch (Throwable t) {
            state = Status.State.UNKNOWN;
            t.printStackTrace(System.out);
        }
        // Only update when server is running, ie. state is something other than null
        if (state != null && status().state() != state) {
            
        	setStatus(state);
            // Workflow just completed
            if (state == Status.State.TERMINATED) {
            	this.setProgress(100, 100);
            	keplerClient().shutdown();
            }
            // Workflow failed so set its progress to 0 and add a new entry to 
            // the log indicating when was this recorded and why workflow failed
            else if (state == Status.State.FAILED) {
            	this.setProgress(0, 100);
            	String message = keplerClient().geterrors();
            	this.log().addEntry(EventType.ERROR, message);
            	keplerClient().shutdown();
            }
            commitChanges();
        }
    }

}
