package transform.mf;


public interface Actor {

    public static enum ActorType {
        role, user;
        public static ActorType fromString(String s) {
            if (role.toString().equalsIgnoreCase(s)) {
                return role;
            } else if (user.toString().equalsIgnoreCase(s)) {
                return user;
            } else {
                return null;
            }
        }
    }

    ActorType actorType();

    String actorName();

}
