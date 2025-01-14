package multiPaxosV1;

import java.util.concurrent.locks.ReentrantLock;

public class Acceptor extends Node {
    private int promisedId = -1;
    private int acceptedId = -1;
    private String acceptedValue = null;
    private ReentrantLock lock = new ReentrantLock();

    public Acceptor(int nodeId) {
        super(nodeId);
    }

    public synchronized Message handlePrepare(int proposalId) {
        lock.lock();
        try {
            if (proposalId > promisedId) {
                promisedId = proposalId;
                return new Message(proposalId, acceptedValue, MessageType.PROMISE);
            }
            return null; // Reject
        } finally {
            lock.unlock();
        }
    }

    public synchronized Message handleAccept(int proposalId, String value) {
        lock.lock();
        try {
            if (proposalId >= promisedId) {
                promisedId = proposalId;
                acceptedId = proposalId;
                acceptedValue = value;
                return new Message(proposalId, value, MessageType.ACCEPTED);
            }
            return null; // Reject
        } finally {
            lock.unlock();
        }
    }

    public String getAcceptedValue() {
        return acceptedValue;
    }
}
