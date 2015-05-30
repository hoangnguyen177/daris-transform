package transform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import transform.mf.ACL;
import transform.mf.ACL.Access;
import transform.mf.SecureIdentityToken;
import transform.mf.User;
import transform.util.DateTimeUtil;
import arc.mf.plugin.PluginService;
import arc.utils.DateTime;
import arc.xml.XmlDoc;
import arc.xml.XmlDocMaker;
import arc.xml.XmlWriter;

/**
 * The transform (instance) class. It is to hold the runtime information of a
 * running transform.
 * 
 * @author wilson
 * 
 */
public abstract class Transform extends Entity {

    /**
     * The document type for the transform.
     */
    public static final String DOC_TYPE = "transform:transform";

    /**
     * The name of the unique id. Used by the unique id to generate unique id
     * for new transform instances.
     */
    public static final String UID_NAME = "transform:transform";

    /**
     * The asset namespace for transforms.
     */
    public static final String NAMESPACE = Application.NAMESPACE + "/instance";

    public static final String OUTPUT_RELATIONSHIP = "transform-output-of";

    public static final String PROPERTY_SECURE_IDENTITY_TOKEN = "secure.identity.token";

    /**
     * The status of the transform.
     * 
     * 
     */
    public static class Status {
        /**
         * Enums of transform states.
         * 
         * 
         */
        public static enum State {
            PENDING, RUNNING, SUSPENDED, TERMINATED, FAILED, UNKNOWN;

            public String toString() {
                return super.toString().toLowerCase();
            }

            /**
             * Parses the state from string.
             * 
             * @param s
             * @return
             */
            public static State fromString(String s) {
                if (s != null) {
                    State[] vs = values();
                    for (int i = 0; i < vs.length; i++) {
                        if (vs[i].toString().equalsIgnoreCase(s)) {
                            return vs[i];
                        }
                    }
                }
                return null;
            }

            public static String[] stringValues() {
                State[] vs = values();
                String[] svs = new String[vs.length];
                for (int i = 0; i < vs.length; i++) {
                    svs[i] = vs[i].toString();
                }
                return svs;
            }
        }

        /**
         * The time when the state is captured.
         */
        private Date _time;
        /**
         * The state of the transform.
         */
        private State _state;

        /**
         * Constructor.
         * 
         * @param state
         *            the state of the transform.
         * @param time
         *            the time when the state is captured.
         */
        public Status(State state, Date time) {
            _state = state;
            _time = time;
        }

        /**
         * Constructor.
         * 
         * @param se
         *            the XML element represents the transform status.
         * @throws Throwable
         */
        public Status(XmlDoc.Element se) throws Throwable {
            _state = State.fromString(se.value());
            if (_state == null) {
                throw new Exception("Invalid state " + se.value());
            }
            _time = se.dateValue("@time");
        }

        /**
         * The state of the transform.
         * 
         * @return
         */
        public State state() {
            return _state;
        }

        /**
         * The time when the state is captured.
         * 
         * @return
         */
        public Date time() {
            return _time;
        }

        /**
         * Saves the status as XML.
         * 
         * @param w
         *            the XML writer.
         * @throws Throwable
         */
        public void save(XmlWriter w) throws Throwable {
            w.add("status", new String[] { "time", new SimpleDateFormat(DateTime.DATE_TIME_FORMAT).format(_time) },
                    _state);
        }

        /**
         * Static method to instantiate the status from the XML element
         * represents it.
         * 
         * @param se
         *            the XML element represents the status.
         * @return
         * @throws Throwable
         */
        public static Status instantiate(XmlDoc.Element se) throws Throwable {
            if (se == null) {
                return null;
            }
            return new Status(se);
        }

    }

    /**
     * The log for the transform.
     * 
     * @author Wei Liu (wliu1976@gmail.com)
     * 
     */
    public static class Log {

        /**
         * The event types.
         */
        public static enum EventType {
            ERROR, WARNING, INFO;

            @Override
            public String toString() {
                return super.toString().toLowerCase();
            }

            /**
             * Parses event type from the given string.
             * 
             * @param s
             * @return
             */
            public static EventType fromString(String s) {
                if (s != null) {
                    EventType[] vs = values();
                    for (int i = 0; i < vs.length; i++) {
                        if (vs[i].toString().equalsIgnoreCase(s)) {
                            return vs[i];
                        }
                    }
                }
                return null;
            }

            public static String[] stringValues() {
                EventType[] vs = values();
                String[] svs = new String[vs.length];
                for (int i = 0; i < vs.length; i++) {
                    svs[i] = vs[i].toString();
                }
                return svs;
            }
        }

        /**
         * The log entry.
         * 
         * 
         */
        public static class Entry {

            private EventType _type;
            private Date _time;
            private String _msg;

            /**
             * Constructor.
             * 
             * @param le
             *            the XML element represents the log entry.
             * @throws Throwable
             */
            public Entry(XmlDoc.Element le) throws Throwable {
                this(EventType.fromString(le.value("@type")), le.dateValue("@time"), le.value());
            }

            /**
             * Constructor.
             * 
             * @param type
             *            the event type.
             * @param time
             *            the time when the event occurs.
             * @param msg
             *            the message about the event.
             */
            public Entry(EventType type, Date time, String msg) {
                _type = type;
                _time = time;
                _msg = msg;
            }

            /**
             * The type of the event.
             * 
             * @return
             */
            public EventType type() {
                return _type;
            }

            /**
             * The time when the event occurs.
             * 
             * @return
             */
            public Date time() {
                return _time;
            }

            /**
             * The message about event.
             * 
             * @return
             */
            public String message() {
                return _msg;
            }

            /**
             * Saves the log entry as XML element.
             * 
             * @param w
             * @throws Throwable
             */
            public void save(XmlWriter w) throws Throwable {
                w.add("log", new String[] { "type", type().toString(), "time",
                        new SimpleDateFormat(DateTime.DATE_TIME_FORMAT).format(time()) }, message());
            }

        }

        private List<Entry> _entries;

        /**
         * Constructor.
         * 
         * @param les
         *            The XML elements that represents the log entries.
         * @throws Throwable
         */
        private Log(List<XmlDoc.Element> les) throws Throwable {
            if (les != null) {
                _entries = new ArrayList<Entry>(les.size());
                for (XmlDoc.Element le : les) {
                    _entries.add(new Entry(le));
                }
            }
        }

        private Log() {
        }

        /**
         * The log entries.
         * 
         * @return
         */
        public List<Entry> entries() {
            return _entries;
        }

        /**
         * Adds a log entry using the current time.
         * 
         * @param type
         *            the event type of the log entry.
         * @param msg
         *            the message.
         */
        public void addEntry(EventType type, String msg) {
            if (_entries == null) {
                _entries = new ArrayList<Entry>();
            }
            _entries.add(new Entry(type, new Date(), msg));
        }

        /**
         * Saves the log to as XML.
         * 
         * @param w
         *            the XML writer.
         * @throws Throwable
         */
        public void save(XmlWriter w) throws Throwable {
            if (_entries != null && !_entries.isEmpty()) {
                for (Entry entry : _entries) {
                    entry.save(w);
                }
            }
        }

        /**
         * If the log has any entries.
         * 
         * @return
         */
        public boolean isEmpty() {
            return _entries != null || _entries.isEmpty();
        }

        /**
         * Instantiates the log from a list of XML elements represent the log
         * entries.
         * 
         * @param les
         *            the list of XML elements represent the log entries.
         * @return
         * @throws Throwable
         */
        public static Log instantiate(List<XmlDoc.Element> les) throws Throwable {
            if (les != null && !les.isEmpty()) {
                return new Log(les);
            } else {
                return new Log();
            }
        }
    }

    /**
     * The progress of the transform.
     * 
     * @author Wei Liu (wliu1976@gmail.com)
     * 
     */
    public static class Progress {
        private int _progress;
        private int _total;
        private Date _time;

        /**
         * Constructor.
         * 
         * @param pe
         *            the XML element represents the progress object.
         * @throws Throwable
         */
        private Progress(XmlDoc.Element pe) throws Throwable {
            this(pe.intValue(), pe.intValue("@total"), pe.dateValue("@time"));
        }

        /**
         * Constructor.
         * 
         * @param progress
         *            the number represents the progress.
         * @param total
         * @param time
         *            the time when the progress is made.
         */
        private Progress(int progress, int total, Date time) {
            _progress = progress;
            _total = total;
            _time = time;
        }

        /**
         * The number represents the total amount.
         * 
         * @return
         */
        public int total() {
            return _total;
        }

        /**
         * The number represents the progress.
         * 
         * @return
         */
        public int progress() {
            return _progress;
        }

        /**
         * The time when the progress is made.
         * 
         * @return
         */
        public Date time() {
            return _time;
        }

        /**
         * Saves the progress object as XML.
         * 
         * @param w
         *            the XML writer.
         * @throws Throwable
         */
        public void save(XmlWriter w) throws Throwable {
            w.add("progress", new String[] { "total", Integer.toString(_total), "time",
                    new SimpleDateFormat(DateTime.DATE_TIME_FORMAT).format(time()) }, Integer.toString(_progress));
        }

        /**
         * Instantiates the givem XML element as progress object.
         * 
         * @param pe
         *            the XML element represents the progress.
         * @return
         * @throws Throwable
         */
        public static Progress instantiate(XmlDoc.Element pe) throws Throwable {
            if (pe != null) {
                return new Progress(pe);
            }
            return null;
        }
    }

    private TransformRepository _repo;
    private long _defnUid;
    private int _defnVersion;
    private Map<String, Parameter> _params;
    private Map<String, String> _properties;
    private Status _status;
    private Progress _progress;
    private XmlDoc.Element _progressDetail;
    private Log _log;

    /**
     * Constructor.
     * 
     * @param ae
     *            the XML element represents the transform asset. It must be the
     *            asset element returned by asset.get or asset.query with
     *            get-meta action.
     * @throws Throwable
     */
    protected Transform(TransformRepository repo, XmlDoc.Element ae) throws Throwable {
        super(ae);
        _repo = repo;
        XmlDoc.Element te = ae.element("meta/" + docType());
        _defnUid = te.longValue("definition");
        _defnVersion = te.intValue("definition/@version", 0);
        _params = Parameter.instantiateMap(te.elements("parameter"));
        _status = Status.instantiate(te.element("status"));
        _progress = Progress.instantiate(te.element("progress"));
        _progressDetail = te.element("progress-detail");
        _log = Log.instantiate(te.elements("log"));
        if (te.elementExists("runtime/property")) {
            List<XmlDoc.Element> rpes = te.elements("runtime/property");
            _properties = new HashMap<String, String>(rpes.size());
            for (XmlDoc.Element rpe : rpes) {
                _properties.put(rpe.value("@name"), rpe.value());
            }
        }
    }

    /**
     * The constructor.
     * 
     * @param repo
     *            the associated transform repository.
     * @param defn
     *            the transform definition.
     * @param uid
     *            the unique identifier.
     * @param name
     *            the name of the transform.
     * @param description
     *            the description of the transform.
     * @param params
     *            the parameters for the transform.
     * @param properties
     *            the runtime properties for the transform.
     * @param input
     *            the content input stream for the transform.
     * @throws Throwable
     */
    protected Transform(TransformRepository repo, TransformDefinition defn, long uid, String name, String description,
            List<Parameter> params, Map<String, String> properties, PluginService.Input input) throws Throwable {
        super(uid, name, description, defn.provider(), Transform.NAMESPACE, input, defaultAcls());
        _repo = repo;
        _defnUid = defn.uid();
        _defnVersion = defn.version();
        if (params != null && !params.isEmpty()) {
            _params = new HashMap<String, Parameter>(params.size());
            for (Parameter param : params) {
                _params.put(param.name(), param);
            }
        }
        _properties = properties;
        _status = new Status(Status.State.PENDING, new Date());
        _log = new Log();
        /*
         * generate secure identity token
         */
        setSecureIdentityToken(SecureIdentityToken.create(_repo.executor()));
    }

    /**
     * The transform provider the object is supported by.
     * 
     * @return
     * @throws Throwable
     */
    public TransformProvider provider() throws Throwable {
        return TransformProviderRegistry.getTransformProviderInstance(providerType(), executor());
    }

    @Override
    public final String docType() {
        return Transform.DOC_TYPE;
    }

    /**
     * The definition of the transform.
     * 
     * @return
     * @throws Throwable
     */
    public TransformDefinition definition() throws Throwable {
        return TransformDefinitionRepository.getInstance(executor()).get(_defnUid, _defnVersion);
    }

    /**
     * The parameters (with values) for the transform.
     * 
     * @return
     */
    public List<Parameter> parameters() {
        if (_params != null && !_params.isEmpty()) {
            return new ArrayList<Parameter>(_params.values());
        }
        return null;
    }

    public Parameter parameter(String name) {
        if (_params != null) {
            return _params.get(name);
        } else {
            return null;
        }
    }

    public String parameterValue(String name) {
        Parameter param = parameter(name);
        if (param != null) {
            return param.value();
        } else {
            return null;
        }
    }

    /**
     * Sets the runtime property.
     * 
     * @param name
     *            the name of the property.
     * @param value
     *            the value of the property.
     */
    public void setRuntimeProperty(String name, String value) {
        if (_properties == null) {
            _properties = new HashMap<String, String>();
        }
        _properties.put(name, value);
    }

    /**
     * Gets the runtime property.
     * 
     * @param name
     *            the name of the property.
     * @return
     */
    public String runtimeProperty(String name) {
        if (_properties == null) {
            return null;
        }
        return _properties.get(name);
    }

    /**
     * Removes the given property.
     * 
     * @param name
     *            the name of the property.
     */
    public void removeRuntimeProperty(String name) {
        if (_properties != null) {
            _properties.remove(name);
        }
    }

    /**
     * Gets the secure identity token associated with the running transform.
     * 
     * @return
     */
    public String secureIdentityToken() {
        return runtimeProperty(PROPERTY_SECURE_IDENTITY_TOKEN);
    }

    /**
     * Sets the secure identity token for the transform.
     * 
     * @param token
     *            the identity token
     */
    public void setSecureIdentityToken(String token) {
        setRuntimeProperty(PROPERTY_SECURE_IDENTITY_TOKEN, token);
    }

    /**
     * The status of the transform.
     * 
     * @return
     */
    public Status status() {
        return _status;
    }

    /**
     * Sets the transform status.
     * 
     * @param state
     * @throws Throwable
     */
    public void setStatus(Status.State state) throws Throwable {
    	// Don't update terminated transforms. Leave them terminated
    	if ( _status.state() != Status.State.TERMINATED ) {
    		_status = new Status(state, new Date());
    	}
        
    }
    
    public void destroyToken() throws Throwable {
    	String token = secureIdentityToken();
    	if (token != null) {
            removeRuntimeProperty(PROPERTY_SECURE_IDENTITY_TOKEN);
            SecureIdentityToken.destroy(executor(), token);
        }
    }

    /**
     * The progress of the transform.
     * 
     * @return
     */
    public Progress progress() {
        return _progress;
    }

    /**
     * Sets the progress.
     */
    public void setProgress(int progress, int total) {
        _progress = new Progress(progress, total, new Date());
    }

    public XmlDoc.Element progressDetail() {
        return _progressDetail;
    }

    public void setProgressDetail(XmlDoc.Element progressDetail) throws Throwable {
        if (progressDetail != null && progressDetail.hasSubElements()) {
            XmlDocMaker dm = new XmlDocMaker("progress-detail", new String[] { "time",
                    DateTimeUtil.formatDateTime(new Date()) });
            dm.add(progressDetail, false);
            _progressDetail = dm.root();
        } else {
            _progressDetail = null;
        }
    }

    /**
     * The log for the transform.
     * 
     * @return
     */
    public Log log() {
        return _log;
    }

    /**
     * Adds output relationship from the given output asset.
     * 
     * @param outputAssetId
     *            the asset id of the output.
     * @throws Throwable
     */
    public void addOutput(String outputAssetId) throws Throwable {
        addRelationshipFrom(OUTPUT_RELATIONSHIP, outputAssetId);
    }

    /**
     * Removes output relationship from the given output asset.
     * 
     * @param outputAssetId
     *            the asset id of the output.
     * @throws Throwable
     */
    public void removeOutput(String outputAssetId) throws Throwable {
        removeRelationshipFrom(OUTPUT_RELATIONSHIP, outputAssetId);
    }

    @Override
    public void save(XmlWriter w, boolean doc) throws Throwable {
        if (doc) {
            w.push(docType());
        }
        w.add("uid", uid());
        w.add("type", providerType());
        w.add("definition", new String[] { "version", Integer.toString(_defnVersion) }, _defnUid);
        if (name() != null) {
            w.add("name", name());
        }
        if (description() != null) {
            w.add("description", description());
        }
        if (_status != null) {
            _status.save(w);
        }
        if (_log != null) {
            _log.save(w);
        }
        if (_progress != null) {
            _progress.save(w);
        }
        if (_progressDetail != null) {
            w.add(_progressDetail, true);
        }
        if (_params != null && !_params.isEmpty()) {
            for (Parameter p : _params.values()) {
                if (p != null) {
                    p.save(w);
                }
            }
        }
        if (_properties != null && !_properties.isEmpty()) {
            w.push("runtime");
            for (String name : _properties.keySet()) {
                w.add("property", new String[] { "name", name }, _properties.get(name));
            }
            w.pop();
        }
        if (doc) {
            w.pop();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected TransformRepository repository() {
        return _repo;
    }

    /**
     * Resets the transform to initial (pending) state (Also remove the log
     * entries and runtime properties).
     * 
     * @throws Throwable
     */
    public void reset() throws Throwable {
        setStatus(Status.State.PENDING);
        _log = new Log();
        _properties = null;
        _progress = null;
    }

    /**
     * Executes the transform.
     * 
     * @throws Throwable
     */
    public abstract void execute() throws Throwable;

    /**
     * Suspends the transform.
     * 
     * @throws Throwable
     */
    public abstract void suspend() throws Throwable;

    /**
     * Resumes the transform.
     * 
     * @throws Throwable
     */
    public abstract void resume() throws Throwable;

    /**
     * Terminates the transform.
     * 
     * @throws Throwable
     */
    public abstract void terminate() throws Throwable;

    /**
     * Updates the status of the transform.
     * 
     * @throws Throwable
     */
    public abstract void updateStatus() throws Throwable;

    public static Map<String, String> parseRuntimeProperties(XmlDoc.Element re) throws Throwable {
        if (re != null && re.elements("property") != null) {
            List<XmlDoc.Element> pes = re.elements("property");
            if (pes != null && !pes.isEmpty()) {
                Map<String, String> properties = new HashMap<String, String>();
                for (XmlDoc.Element pe : pes) {
                    properties.put(pe.value("@name"), pe.value());
                }
                return properties;
            }
        }
        return null;
    }

    public static List<ACL> defaultAcls() throws Throwable {
        List<ACL> acls = new ArrayList<ACL>();
        acls.add(new ACL(User.self(), Access.READ_WRITE));
        acls.add(new ACL(Roles.ADMIN_ROLE, Access.READ_WRITE));
        acls.add(new ACL(Roles.DEVELOPER_ROLE, Access.READ));
        acls.add(new ACL(Roles.USER_ROLE, Access.READ));
        return acls;
    }

}
