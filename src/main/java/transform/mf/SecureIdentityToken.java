package transform.mf;

import java.util.Collection;

import arc.mf.plugin.ServiceExecutor;
import arc.xml.XmlDoc;
import arc.xml.XmlDocMaker;

public class SecureIdentityToken {

    public static String create(ServiceExecutor executor) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        // TODO: ask Slavisa to supply app option in the Mediaflux actor. so that we can enable app restriction.
        // dm.add("app", Application.NAME);
        
        // Work around below bug
        Collection<String> roles = roles (executor);
        for (String role : roles) {
        	dm.add("role",  new String[] { "type", "role" }, role);
        }

        dm.add("role", new String[] { "type", "user" }, User.self().actorName());
        String token = executor.execute("secure.identity.token.create", dm.root()).value("token");
        return token;
    }

    public static void destroy(ServiceExecutor executor, String token) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("token", token);
        executor.execute("secure.identity.token.destroy", dm.root());
    }
    
    private static Collection<String> roles (ServiceExecutor executor) throws Throwable {
    	XmlDoc.Element r = executor.execute("actor.self.describe");
    	return r.values("actor/role[@type='role']");
    }
}
