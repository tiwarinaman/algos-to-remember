package multiPaxosV1;

public enum MessageType {
    PREPARE,
    PROMISE,
    ACCEPT,
    ACCEPTED,
    HEARTBEAT
}
