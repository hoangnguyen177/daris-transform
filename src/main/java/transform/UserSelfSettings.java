package transform;

import arc.mf.plugin.PluginThread;
import arc.mf.plugin.ServiceExecutor;
import arc.xml.XmlDoc;
import arc.xml.XmlDocMaker;
import arc.xml.XmlDocWriter;
import arc.xml.XmlWriter;

/**
 * The UserSettings class contains static methods to get/set user settings using
 * the service user.self.settings.get/set services.
 * 
 * @author Wei Liu (wliu1976@gmail.com)
 * 
 */
public class UserSelfSettings {

    /**
     * Updates the user settings with given XML element.
     * 
     * @param executor
     *            the service executor
     * @param se
     *            the XML element represents the user settings.
     * @throws Throwable
     */
    public static void set(ServiceExecutor executor, XmlDoc.Element se) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        XmlWriter w = new XmlDocWriter(dm);
        w.add("app", Application.NAME);
        w.push("settings");
        w.add(se, false);
        w.pop();
        executor.execute("user.self.settings.set", dm.root());
    }

    /**
     * Updates the user settings with given XML element.
     * 
     * @param se
     *            the XML element represents the user settings.
     * @throws Throwable
     */
    public static void set(XmlDoc.Element se) throws Throwable {
        set(PluginThread.serviceExecutor(), se);
    }

    /**
     * Gets the user settings.
     * 
     * @param executor
     *            the service executor
     * @return
     * @throws Throwable
     */
    public static XmlDoc.Element get(ServiceExecutor executor) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("app", Application.NAME);
        return executor.execute("user.self.settings.get", dm.root()).element("settings");
    }

    /**
     * Gets the user settings.
     * 
     * @return
     * @throws Throwable
     */
    public static XmlDoc.Element get() throws Throwable {
        return get(PluginThread.serviceExecutor());
    }

    /**
     * Reset/clear the user settings.
     * 
     * @param executor
     *            the service executor
     * @throws Throwable
     */
    public static void reset(ServiceExecutor executor) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("app", Application.NAME);
        executor.execute("user.self.settings.remove", dm.root());
    }

    /**
     * Reset/clear the user settings.
     * 
     * @throws Throwable
     */
    public static void reset() throws Throwable {
        reset(PluginThread.serviceExecutor());
    }
}
