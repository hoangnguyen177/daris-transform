package transform.kepler;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

import arc.mf.plugin.PluginLog;
import transform.Transform.Status.State;

public class KeplerClient {

    public static final String LOCALHOST = "localhost";

    public static final String RESPONSE_OK = "OK";

    public static final String LOG_NAME = "kepler-client";

    public static enum Command {
        close, status, terminate, suspend, resume, start, shutdown, progress, geterrors;
        public static Command fromString(String s) {
            if (s != null) {
                Command[] vs = values();
                for (int i = 0; i < vs.length; i++) {
                    if (vs[i].toString().equalsIgnoreCase(s)) {
                        return vs[i];
                    }
                }
            }
            return null;
        }
    }

    private String _serverHost;

    private int _serverPort;

    private long _uid;

    public KeplerClient(String serverHost, int serverPort, long uid) {
        _serverHost = serverHost;
        _serverPort = serverPort;
        _uid = uid;
    }

    public KeplerClient(int serverPort, long uid) {
        this(LOCALHOST, serverPort, uid);
    }

    public State status() throws Throwable {
        return State.fromString(execute(_serverHost, _serverPort, Command.status, this._uid, null));
    }
    
    public String geterrors() throws Throwable {
    	String response = execute(_serverHost, _serverPort, Command.geterrors, this._uid, null);
    	return response;
    }

    public int progress() throws Throwable {
    	String response = execute(_serverHost, _serverPort, Command.progress, this._uid, null);
    	
    	if (response == null || response.trim().length() == 0)
    		response = "-1";
    	return new Integer(response.trim()).intValue();
    }
    
    public boolean terminate() throws Throwable {
        String response = execute(_serverHost, _serverPort, Command.terminate, this._uid, null);
        this.shutdown();
        return RESPONSE_OK.equalsIgnoreCase(response);
    }

    public boolean suspend() throws Throwable {
        String response = execute(_serverHost, _serverPort, Command.suspend, this._uid, null);
        return RESPONSE_OK.equalsIgnoreCase(response);
    }

    public boolean resume() throws Throwable {
        String response = execute(_serverHost, _serverPort, Command.resume, this._uid, null);
        return RESPONSE_OK.equalsIgnoreCase(response);
    }

    public boolean start(String xmlContent) throws Throwable {
        String response = execute(_serverHost, _serverPort, Command.start, this._uid, xmlContent);
        boolean ok = RESPONSE_OK.equalsIgnoreCase(response);
        if (!ok) {
            System.out.println("Failed start remote kepler server(" + _serverHost + ": " + _serverPort
                    + "). Response: " + response);
        }
        return ok;
    }

    public boolean shutdown() throws Throwable {
        String response = execute(_serverHost, _serverPort, Command.shutdown, this._uid, null);
        return RESPONSE_OK.equalsIgnoreCase(response);
    }

    public static String execute(String serverHost, int serverPort, Command command, Long uid, String content)
            throws Throwable {
        logInfo("Executing command: " + command + " on remote Kepler server: " + serverHost + ":" + serverPort);
        Selector selector = SelectorProvider.provider().openSelector();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(serverHost, serverPort));
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        String response = null;
        boolean done = false;
        while (!done) {
            selector.select();
            for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
                SelectionKey key = it.next();
                it.remove();
                if (!key.isValid()) {
                    continue;
                }
                if (key.isConnectable()) {
                    done = !connect(key);
                } else if (key.isWritable()) {
                    String tosend = command.toString().concat("::").concat(uid.toString());
                    if (content != null) {
                        tosend = tosend.concat("::BEGINFILE").concat(content).concat("ENDFILE");
                    }

                    write(key, tosend);
                    // key.interestOps(SelectionKey.OP_READ);
                    if (command == Command.close) {
                        key.channel().close();
                        done = true;
                        selector.wakeup();
                    }

                } else if (key.isReadable()) {
                    response = read(key);
                    if (response == null) {
                        response = State.TERMINATED.toString();
                        done = true;
                        continue;
                    }
                    if (command == Command.status) {
                        if (State.fromString(response) == null) {
                            throw new Exception("Invalid status: " + response);
                        }

                    } else if (command == Command.progress) {
                    	try {
                    		new Integer(response);
                    	} catch (Throwable e) {
                    		throw new Exception("Invalid progress value: '" + response + "'");
                    	}
                	} else if (command == Command.geterrors) {
                		response = response.replace("::", "\n");
                	} else {
                        if (!response.equalsIgnoreCase(RESPONSE_OK)) {
                            throw new Exception("Invalid response: " + response);
                        }
                    }
                    command = Command.close;
                    content = null;
                    key.interestOps(SelectionKey.OP_WRITE);
                }
            }
        }
        if (socketChannel.isOpen()) {
            socketChannel.close();
        }
        if (selector.isOpen()) {
            selector.close();
        }

        logInfo("Remote Kepler server response: " + response);
        return response;
    }

    private static boolean connect(SelectionKey key) throws Throwable {
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            while (!sc.finishConnect()) {
                Thread.sleep(100);
            }
            key.interestOps(SelectionKey.OP_WRITE);
            return true;
        } catch (Throwable e) {
            key.cancel();
            return false;
        }
    }

    private static void write(SelectionKey key, String command) throws Throwable {

        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer bb = ByteBuffer.wrap(command.getBytes());
        while (bb.remaining() > 0) {
            sc.write(bb);
        }
        key.interestOps(SelectionKey.OP_READ);
    }

    private static String read(SelectionKey key) throws Throwable {
        String response = null;
        ByteBuffer bb = ByteBuffer.allocate(8096);
        bb.clear();
        SocketChannel sc = (SocketChannel) key.channel();
        int bytesRead;
        try {
            bytesRead = sc.read(bb);
        } catch (Throwable e) {
            bytesRead = -1;
        }
        if (bytesRead == -1) {
            // connection closed from server side
            key.cancel();
            sc.close();
        } else {
            bb.flip();
            byte[] bytes = new byte[bb.remaining()];
            bb.get(bytes);
            response = (new String(bytes)).trim();
        }
        return response;
    }

    public static void main(String[] args) throws Throwable {
        int id = 4;
        KeplerClient kc = new KeplerClient("localhost", 41000, id);
        File karFile = new File("/Users/slavisa/ParameterTest.xml");
        KeplerXML kxml = new KeplerXML(karFile);
        kc.start(KeplerXML.DTD + kxml.root().toString());
        // System.out.println(kc.status().toString() + " " + id);
        // System.out.println(kc.status().toString() + " " + id);
        // System.out.println(kc.status().toString() + " " + id);
        // System.out.println(kc.status().toString() + " " + id);
        String status = kc.status().toString().trim();
        if (status.equalsIgnoreCase("failed")) {
    		kc.geterrors();
    	}
        while (status.compareTo("terminated") != 0 && status.compareTo("failed") != 0) {
        	Thread.sleep(5000);
        	kc.progress();
        	status = kc.status().toString().trim();
        	if (status.equalsIgnoreCase("failed")) {
        		kc.geterrors();
        	}
        }
    }

    public static PluginLog logger() {
        return PluginLog.log(LOG_NAME);
    }

    public static void logError(String message, Throwable error) {
        PluginLog logger = logger();
        logger.add(PluginLog.ERROR, message, error);
    }

    public static void logInfo(String message) {
        PluginLog logger = logger();
        logger.add(PluginLog.INFORMATION, message);
    }

}
