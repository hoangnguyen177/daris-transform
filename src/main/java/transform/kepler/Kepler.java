package transform.kepler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import transform.Application;

public class Kepler {

    public static final String PROPERTY_KEPLER_LOCAL_HOME = "kepler.local.home";

    public static final String PROPERTY_KEPLER_SERVER_HOST = "kepler.server.host";

    public static final String PROPERTY_KEPLER_SERVER_PORT = "kepler.server.port";

    public static final String FILE_KEPLER_SH = "kepler.sh";

    public static final String FILE_KEPLERNK_SH = "keplernk.sh";

    public static final String FILE_KEPLER_JAR = "kepler.jar";

    public static final int DEFAULT_SERVER_PORT = 41000;

    /**
     * Returns the path to Kepler home, where the kepler.sh and kepler.jar is located.
     * 
     * @return
     */
    public static String localHome() throws Throwable {

        String keplerHome = Application.getProperty(PROPERTY_KEPLER_LOCAL_HOME);
        if (keplerHome == null) {
            throw new NullPointerException(
                    "kepler.home is not set. Run transform.kepler.home.set service to set the kepler location.");
        }
        return keplerHome;
    }

    /**
     * Sets the Kepler home directory, where the kepler.sh and kepler.jar is located.
     * 
     * @param path
     * @throws Throwable
     */
    public static void setLocalHome(String path) throws Throwable {

        /*
         * validate the kepler home directory
         */
        if (path == null) {
            throw new NullPointerException("Kepler home is null.");
        }
        File dir = new File(path);
        if (!dir.exists()) {
            throw new FileNotFoundException("Could not find " + dir.getAbsolutePath());
        }
        if (!dir.isDirectory()) {
            throw new Exception(path + " is not a valid directory");
        }
        File keplerSh = new File(dir, FILE_KEPLER_SH);
        if (!keplerSh.exists()) {
            throw new FileNotFoundException("Could not find " + keplerSh.getAbsolutePath());
        }
        File keplernkSh = new File(dir, FILE_KEPLERNK_SH);
        if (!keplernkSh.exists()) {
            throw new FileNotFoundException("Could not find " + keplernkSh.getAbsolutePath());
        }
        File keplerJar = new File(dir, FILE_KEPLER_JAR);
        if (!keplerJar.exists()) {
            throw new FileNotFoundException("Could not find " + keplerJar.getAbsolutePath());
        }

        /*
         * set the kepler home directory
         */
        Application.setProperty(PROPERTY_KEPLER_LOCAL_HOME, path);
    }

    /**
     * Executes a workflow (definition xml file) and returns a Process object.
     * 
     * @param keplerXmlFilePath
     *            file path to the workflow definition xml file.
     * @param redirectErrorStream
     *            choose if redirect the stderr to stdout stream.
     * @return The Process object.
     * @throws Throwable
     */
    public static Process execute(String keplerXmlFilePath, boolean redirectErrorStream) throws Throwable {
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", Kepler.localHome() + File.separator + "keplernk.sh "
                + keplerXmlFilePath);
        pb.redirectErrorStream(redirectErrorStream);
        pb.directory(new File(localHome()));
        return pb.start();
    }

    /**
     * Executes a kepler workflow xml file and returns the exit value of the process.
     * 
     * @param keplerXml
     *            the kepler workflow definition xml file.
     * @param stdout
     *            the output file to save the stdout and stderr of the kepler process.
     * @return the exit value (integer) of the kepler process.
     * @throws Throwable
     */
    public static int execute(File keplerXml, File stdout) throws Throwable {
        Process proc = execute(keplerXml.getAbsolutePath(), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = null;
        PrintStream ps = new PrintStream(stdout);
        try {
            while ((line = reader.readLine()) != null) {
                ps.println(line);
            }
        } finally {
            ps.close();
        }
        return proc.waitFor();
    }

}
