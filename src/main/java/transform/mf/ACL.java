package transform.mf;

import transform.mf.Actor.ActorType;
import arc.xml.CanSaveToXml;
import arc.xml.XmlDoc;
import arc.xml.XmlWriter;

public class ACL implements CanSaveToXml {

    public static enum Access {
        NONE, READ, READ_WRITE;

        @Override
        public String toString() {
            return super.toString().replace('_', '-').toLowerCase();
        }

        public static Access fromString(String s) {
            if (s != null) {
                Access[] vs = values();
                for (int i = 0; i < vs.length; i++) {
                    if (vs[i].toString().replace('-', '_').equalsIgnoreCase(s)) {
                        return vs[i];
                    }
                }
            }
            return null;
        }
    }

    private Actor _actor;
    private Access _metadata;
    private Access _content;
    private boolean _propagate;

    public ACL(XmlDoc.Element ae) throws Throwable {
        _actor = instantiateActor(ae);
        _metadata = Access.fromString(ae.value("metadata"));
        _content = Access.fromString(ae.value("content"));
        _propagate = ae.booleanValue("propagate", false);
    }

    public ACL(Actor actor, Access metadata, Access content, boolean propagate) {
        _actor = actor;
        _metadata = metadata;
        _content = content;
        _propagate = propagate;
    }

    public ACL(Actor actor, Access metadata, Access content) {
        this(actor, metadata, content, false);
    }

    public ACL(Actor actor, Access access, boolean propagate) {
        this(actor, access, access, propagate);
    }

    public ACL(Actor actor, Access access) {
        this(actor, access, false);
    }

    public Actor actor() {
        return _actor;
    }

    public Access metadataAccess() {
        return _metadata;
    }

    public Access contentAccess() {
        return _content;
    }

    public boolean propagate() {
        return _propagate;
    }

    @Override
    public void save(XmlWriter w) throws Throwable {
        w.push("acl");
        w.add("actor", new String[] { "type", _actor.actorType().toString() }, _actor.actorName());
        if (_metadata != null) {
            w.add("metadata", _metadata);
        }
        if (_content != null) {
            w.add("content", _content);
        }
        if (_propagate) {
            w.add("propagate", true);
        }
        w.pop();
    }

    public Actor instantiateActor(XmlDoc.Element ae) throws Throwable {
        ActorType type = ActorType.fromString(ae.value("@type"));
        String value = ae.value();
        if (type == null) {
            return null;
        }
        switch (type) {
        case role:
            return new Role(value);
        case user:
            int idx = value.lastIndexOf(':');
            String domain = value.substring(0, idx);
            String user = value.substring(idx + 1);
            return new User(domain, user);
        default:
            return null;
        }
    }
}
