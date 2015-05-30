package transform.exec;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import transform.Parameter;
import transform.Transform;
import transform.TransformDefinition;
import transform.TransformRepository;
import transform.util.ObjectUtil;
import transform.util.StreamUtil;
import arc.mf.plugin.PluginService;
import arc.xml.XmlDoc.Element;

public class ExecTransform extends Transform {

    public static final String PARAM_COMMAND = "command";
    public static final String PROPERTY_PID = "pid";
    public static final String TRANSFORM_TYPE = "exec";

    public ExecTransform(TransformRepository repo, Element ae) throws Throwable {
        super(repo, ae);
    }

    public ExecTransform(TransformRepository repo, TransformDefinition defn, long uid, String name, String description,
            List<Parameter> params, Map<String, String> properties, PluginService.Input input) throws Throwable {
        super(repo, defn, uid, name, description, params, properties, input);
    }

    protected String command() {
        return parameterValue(PARAM_COMMAND);
    }

    protected String pid() {
        return runtimeProperty(PROPERTY_PID);
    }

    @Override
    public void terminate() throws Throwable {
        String pid = pid();
        if (pid == null) {
            throw new Exception("Could not find pid of transform " + uid() + ".");
        }
        Exec.get().terminate(Long.parseLong(pid));
    }

    @Override
    public void suspend() throws Throwable {
        String pid = pid();
        if (pid == null) {
            throw new Exception("Could not find pid of transform " + uid() + ".");
        }
        Exec.get().suspend(Long.parseLong(pid));
    }

    @Override
    public void resume() throws Throwable {
        String pid = pid();
        if (pid == null) {
            throw new Exception("Could not find pid of transform " + uid() + ".");
        }
        Exec.get().resume(Long.parseLong(pid));
    }

    @Override
    public void execute() throws Throwable {

        // get the command
        String cmd = command();
        if (cmd == null) {
            PluginService.Output content = contentOutput();
            if (content != null) {
                File tf = PluginService.createTemporaryFile("exec_transform_script_");
                tf.setExecutable(true);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tf));
                InputStream in = content.stream();
                try {
                    StreamUtil.copy(in, out);
                } finally {
                    out.close();
                    in.close();
                }
                cmd = "/bin/bash " + tf.getAbsolutePath();
            } else {
                throw new Exception("Could not find command parameter and there is no script as content.");
            }
        }

        // execute the command
        ProcessBuilder pb = new ProcessBuilder(parseCommandLine(cmd));
        pb.redirectErrorStream(true);
        File wd = PluginService.createTemporaryDirectory();
        pb.directory(wd);

        Process p = pb.start();

        // get the pid
        Field pidField = p.getClass().getDeclaredField("pid");
        pidField.setAccessible(true);
        String pid = pidField.get(p).toString();

        p.waitFor();

        // save pid
        if (pid != null) {
            setRuntimeProperty(PROPERTY_PID, pid);
        }

        // add result (stdout)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(baos)), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
                System.out.println(line);
            }
            writer.flush();
        } finally {
            writer.close();
            reader.close();
        }

        // clean up temporary directory
        wd.delete();
    }

    @Override
    public void updateStatus() throws Throwable {
        String pid = pid();
        if (pid == null) {
            throw new Exception("Could not find pid of transform " + uid() + ".");
        }
        Exec exec = Exec.get();
        String ps = exec.state(Long.parseLong(pid));
        Status.State state = status().state();
        if (ps == null) {
            state = Status.State.TERMINATED;
        } else if (ps.startsWith("T")) {
            state = Status.State.SUSPENDED;
        } else if (ps.startsWith("S") || ps.startsWith("R") || ps.startsWith("D")) {
            state = Status.State.RUNNING;
        } else {
            state = Status.State.TERMINATED;
        }
        if (!ObjectUtil.equals(status().state(), state)) {
            setStatus(state);
            commitChanges();
        }
    }

    private static String[] parseCommandLine(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        List<String> tokens = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.startsWith("\"")) {
                while (tokenizer.hasMoreTokens()) {
                    String token2 = tokenizer.nextToken();
                    token += " " + token2;
                    if (token2.endsWith("\"")) {
                        break;
                    }
                }
            }
            tokens.add(token);
        }
        String[] ts = new String[tokens.size()];
        tokens.toArray(ts);
        return ts;
    }

}
