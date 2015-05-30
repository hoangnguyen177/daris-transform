package transform;

import arc.mf.plugin.PluginThread;
import arc.mf.plugin.ServiceExecutor;
import arc.xml.XmlDoc;
import arc.xml.XmlDocMaker;
import arc.xml.XmlDocWriter;
import arc.xml.XmlWriter;

public class UserSettings {

    /**
     * Sets the given user's specific settings.
     * 
     * @param executor
     *            the service executor.
     * @param domain
     *            the domain of the user
     * @param user
     *            the name of the user
     * @param se
     *            the settings
     * @throws Throwable
     */
    public static void set(ServiceExecutor executor, String domain, String user, XmlDoc.Element se) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        XmlWriter w = new XmlDocWriter(dm);
        w.add("app", Application.NAME);
        w.add("domain", domain);
        w.add("user", user);
        w.push("settings");
        w.add(se, false);
        w.pop();
        executor.execute("user.settings.set", dm.root());
    }

    /**
     * Sets the given user's specific settings.
     * 
     * @param domain
     *            the domain of the user
     * @param user
     *            the name of the user
     * @param se
     *            the settings
     * @throws Throwable
     */
    public static void set(String domain, String user, XmlDoc.Element se) throws Throwable {
        set(PluginThread.serviceExecutor(), domain, user, se);
    }

    /**
     * Gets the given user's specific settings.
     * 
     * @param executor
     *            the service executor
     * @param domain
     *            the domain of the user
     * @param user
     *            the name of the user
     * @return
     * @throws Throwable
     */
    public static XmlDoc.Element get(ServiceExecutor executor, String domain, String user) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("app", Application.NAME);
        dm.add("domain", domain);
        dm.add("user", user);
        return executor.execute("user.settings.get", dm.root()).element("settings");
    }

    /**
     * Gets the given user's specific settings.
     * 
     * @param domain
     *            the domain of the user
     * @param user
     *            the name of the user
     * @return
     * @throws Throwable
     */
    public static XmlDoc.Element get(String domain, String user) throws Throwable {
        return get(PluginThread.serviceExecutor(), domain, user);
    }

    /**
     * Remove sub-settings from the given user's settings. Any sub-element that
     * matches the given element (and sub-elements) will be removed. The match
     * is partial, so multiple elements may be removed at one time by only
     * specifying that portion that needs to match.
     * 
     * @param executor
     *            the service executor
     * @param domain
     *            the domain of the user
     * @param user
     *            the name of the user
     * @param e
     * @throws Throwable
     */
    public static void remove(ServiceExecutor executor, String domain, String user, XmlDoc.Element e) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("app", Application.NAME);
        dm.add("domain", domain);
        dm.add("user", user);
        dm.push("settings");
        dm.add(e);
        dm.pop();
        executor.execute("user.settings.remove.from", dm.root());
    }

    /**
     * Reset the given user's settings.
     * 
     * @param executor
     *            the service executor
     * @param domain
     *            the domain of the user
     * @param user
     *            the name of the user
     * @throws Throwable
     */
    public static void reset(ServiceExecutor executor, String domain, String user) throws Throwable {
        XmlDocMaker dm = new XmlDocMaker("args");
        dm.add("app", Application.NAME);
        dm.add("domain", domain);
        dm.add("user", user);
        executor.execute("user.settings.remove", dm.root());
    }

    /**
     * Reset the given user's settings.
     * 
     * @param domain
     *            the domain of the user
     * @param user
     *            the name of the user
     * @throws Throwable
     */
    public static void reset(String domain, String user) throws Throwable {
        reset(PluginThread.serviceExecutor(), domain, user);
    }

}
