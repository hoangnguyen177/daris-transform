package transform.kepler;

import arc.xml.CanSaveToXml;
import arc.xml.XmlDoc;
import arc.xml.XmlDocMaker;
import arc.xml.XmlDocWriter;
import arc.xml.XmlWriter;

public class KeplerServerSettings implements CanSaveToXml {

    public static class LauncherService implements CanSaveToXml {
        private String _name;
        private XmlDoc.Element _args;
        private String _portXPath;

        public LauncherService(XmlDoc.Element lse) throws Throwable {
            _name = lse.value("@name");
            _args = lse.element("args");
            _portXPath = lse.value("port-xpath");
        }

        public String name() {
            return _name;
        }

        public XmlDoc.Element args() {
            return _args;
        }

        public String portXPath() {
            return _portXPath;
        }

        public void save(XmlWriter w) throws Throwable {
            w.push("launcher-service", new String[] { "name", _name });
            if (_args != null) {
                w.push("args");
                w.add(_args, false);
                w.pop();
            }
            w.add("port-xpath", _portXPath);
            w.pop();
        }

    }

    private String _host;
    private int _port;
    private LauncherService _launcherService;

    public KeplerServerSettings(XmlDoc.Element se) throws Throwable {
        if (se != null) {
            _host = se.value("host");
            _port = se.intValue("port", -1);
            if (se.elementExists("launcher-service")) {
                _launcherService = new LauncherService(se.element("launcher-service"));
            }
        }
    }

    public String host() {
        return _host;
    }

    public void setHost(String host) {
        _host = host;
    }

    public int port() {
        return _port;
    }

    public void setPort(int port) {
        _port = port;
    }

    public LauncherService launcherService() {
        return _launcherService;
    }

    public void setLauncherService(LauncherService launcherService) {
        _launcherService = launcherService;
    }

    public void save(XmlWriter w) throws Throwable {
        w.push(KeplerTransformProvider.USER_SPECIFIC_SETTINGS_KEPLER_SERVER);
        if (_host != null) {
            w.add("host", _host);
        }
        if (_port > 0) {
            w.add("port", _port);
        }
        if (_launcherService != null) {
            _launcherService.save(w);
        }
        w.pop();
    }

    public XmlDoc.Element toXmlElement() throws Throwable {
        XmlDocMaker dm = new XmlDocMaker();
        XmlDocWriter w = new XmlDocWriter(dm);
        save(w);
        return dm.root();
    }
}
