package transform.exec;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.StringTokenizer;

/**
 * A utility class to execute/start a system process. It works (tested) only on
 * Linux and Mac OS.
 * 
 * @author wilson
 * 
 */
public class Exec {

	private static Exec _instance;

	/**
	 * Return the singleton object. If the underlying os is not mac os or linux,
	 * it throws exception.
	 * 
	 * @return
	 * @throws Throwable
	 */
	public static Exec get() throws Throwable {
		if (_instance == null) {
			_instance = new Exec();
		}
		return _instance;
	}

	/**
	 * Check the underlying operating system is Unix like system (Linux/*nix/Mac
	 * OS).
	 * 
	 * @return
	 */
	public static boolean isUnix() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("linux") == -1 && os.indexOf("nix") == -1 && os.indexOf("mac") == -1) {
			return false;
		}
		return true;
	}

	private Exec() throws Throwable {

		if (!isUnix()) {
			throw new Exception(getClass().getName() + " supports only Linux and Mac OS");
		}
	}

	/**
	 * returns the pid of the process.
	 * 
	 * @param process
	 * @return
	 * @throws Throwable
	 */
	public long pid(Process process) throws Throwable {

		Field pidField = process.getClass().getDeclaredField("pid");
		pidField.setAccessible(true);
		return pidField.getLong(process);
	}

	/**
	 * Send SIGCONT to resume the process.
	 * 
	 * @param pid
	 *            the pid of the process.
	 * @throws Throwable
	 */
	public void resume(long pid) throws Throwable {
		exec(true, "/bin/kill", "-CONT", Long.toString(pid));
	}

	/**
	 * Send SIGSTOP to suspend the process.
	 * 
	 * @param pid
	 *            the pid of the process
	 * @throws Throwable
	 */
	public void suspend(long pid) throws Throwable {
		exec(true, "/bin/kill", "-STOP", Long.toString(pid));
	}

	/**
	 * Send SIGKILL to kill the process.
	 * 
	 * @param pid
	 *            the pid of the process
	 * @throws Throwable
	 */
	public void terminate(long pid) throws Throwable {
		exec(true, "/bin/kill", "-KILL", Long.toString(pid));
	}

	/**
	 * Returns the state code of the process. Returns null if the process does
	 * not exist.
	 * 
	 * @param pid
	 *            the pid of process
	 * @return
	 * @throws Throwable
	 */
	public String state(long pid) throws Throwable {

		ProcessBuilder pb = new ProcessBuilder("/bin/ps", "-o", "state", "-p", Long.toString(pid));
		Process p = pb.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		// the first line is the header
		reader.readLine();
		// the second line is the state
		String line = reader.readLine();
		if (line == null) {
			return null;
		}
		if (line.trim().equals("")) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(line);
		String state = st.nextToken();
		return state;
	}

	/**
	 * Execute the command line and returns the Process object.
	 * 
	 * @param wait
	 *            wait until the process finish to return.
	 * @param command
	 *            the command line
	 * @return
	 * @throws Throwable
	 */
	public Process exec(boolean wait, String... command) throws Throwable {

		ProcessBuilder pb = new ProcessBuilder(command);
		Process p = pb.start();
		if (wait) {
			p.waitFor();
		}
		return p;
	}

	/**
	 * executes the command and returns its pid. (It does not wait until the
	 * process complete to return.)
	 * 
	 * @param command
	 *            the command
	 * @return the pid of the system process
	 * @throws Throwable
	 */
	public long exec(String... command) throws Throwable {

		Process p = exec(false, command);
		return pid(p);
	}

}
