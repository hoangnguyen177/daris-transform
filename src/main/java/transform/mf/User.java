package transform.mf;

import arc.mf.plugin.PluginServiceUser;
import arc.mf.plugin.Session;

public class User extends PluginServiceUser implements Actor {

    public User(String domain, String username) {
        super(domain, username);
    }

    @Override
    public ActorType actorType() {
        return ActorType.user;
    }

    @Override
    public String actorName() {
        return domain() + ":" + user();
    }

    @Override
    public String toString() {
        return actorName();
    }

    public static User self() throws Throwable {
        Session.User sessionUser = Session.user();
        return new User(sessionUser.domain(), sessionUser.name());
    }

}
