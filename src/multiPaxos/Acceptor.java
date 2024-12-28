package multiPaxos;

public class Acceptor {

    private int highestProposalId = -1;
    private int acceptedProposalId = -1;
    private String acceptedValue = null;

    public synchronized boolean prepare(int proposalId) {
        if (proposalId > highestProposalId) {
            highestProposalId = proposalId;
            System.out.println("[Acceptor] Prepared proposalId " + proposalId);
            return true;
        }
        System.out.println("[Acceptor] Rejecting proposalId " + proposalId + " due to higher proposalId " + highestProposalId);
        return false;
    }

    public synchronized boolean accept(int proposalId, String value) {
        if (proposalId >= highestProposalId) {
            highestProposalId = proposalId;
            acceptedProposalId = proposalId;
            acceptedValue = value;
            System.out.println("[Acceptor] Accepted proposalId " + proposalId + " with value " + value);
            return true;
        }
        System.out.println("[Acceptor] Rejecting accept for proposalId " + proposalId + " due to higher proposalId " + highestProposalId);
        return false;
    }

    public synchronized String getAcceptedValue() {
        return acceptedValue;
    }

}
