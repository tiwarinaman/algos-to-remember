package multiPaxosV1;

public abstract class Node {

    protected int nodeId;
    protected boolean isLeader;
    protected int currentProposalId;

    public Node(int nodeId) {
        this.nodeId = nodeId;
        this.isLeader = false;
        this.currentProposalId = 0;
    }
}
