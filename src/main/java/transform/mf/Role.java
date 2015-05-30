package transform.mf;

public class Role implements Actor {

    private String _name;

    public Role(String name) {
        _name = name;
    }

    @Override
    public ActorType actorType() {
        return ActorType.role;
    }

    @Override
    public String actorName() {
        return _name;
    }

    public String name() {
        return _name;
    }

    @Override
    public String toString() {
        return _name;
    }

}
