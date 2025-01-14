package multiPaxosV1;

import java.util.List;

public class Proposer extends Node {
    private List<Acceptor> acceptors;
    private int quorum;

    public Proposer(int nodeId, List<Acceptor> acceptors) {
        super(nodeId);
        this.acceptors = acceptors;
        this.quorum = (acceptors.size() / 2) + 1;
    }

    public void propose(String value) {
        if (!isLeader) {
            System.out.println("Node " + nodeId + " is not the leader. Proposal ignored.");
            return;
        }

        currentProposalId++;
        int promises = 0;

        for (Acceptor acceptor : acceptors) {
            Message response = acceptor.handlePrepare(currentProposalId);
            if (response != null && response.type == MessageType.PROMISE) {
                promises++;
            }
        }

        if (promises >= quorum) {
            int acceptances = 0;

            for (Acceptor acceptor : acceptors) {
                Message response = acceptor.handleAccept(currentProposalId, value);
                if (response != null && response.type == MessageType.ACCEPTED) {
                    acceptances++;
                }
            }

            if (acceptances >= quorum) {
                System.out.println("Consensus reached on value: " + value);
            } else {
                System.out.println("Consensus failed during acceptance phase.");
            }
        } else {
            System.out.println("Consensus failed during promise phase.");
        }
    }
}
