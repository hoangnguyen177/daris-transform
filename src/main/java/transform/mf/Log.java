package transform.mf;

import arc.mf.plugin.PluginThread;
import arc.xml.XmlDocMaker;

public class Log {
    private Log() {
    }

    public static enum Event {
        error, info, warning;
    }

    public static void add(String app, Event event, String msg) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("app", app);
        dm.add("event", event);
        dm.add("msg", msg);
        PluginThread.serviceExecutor().execute("server.log", dm.root());
    }
}
