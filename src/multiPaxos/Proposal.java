package multiPaxos;

public class Proposal {

    private final int id;
    private final String value;

    public Proposal(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
