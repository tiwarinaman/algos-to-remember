package multiPaxosV1;

public class Message {
    public int proposalId;
    public String value;
    public MessageType type;

    public Message(int proposalId, String value, MessageType type) {
        this.proposalId = proposalId;
        this.value = value;
        this.type = type;
    }
}
