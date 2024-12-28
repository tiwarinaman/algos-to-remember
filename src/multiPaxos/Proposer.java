package multiPaxos;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Proposer implements Runnable {

    private static final AtomicInteger globalProposalId = new AtomicInteger(0);

    private final String[] valuesToPropose;
    private final int proposerId;
    private final List<Acceptor> acceptors;

    public Proposer(String[] values, int proposerId, List<Acceptor> acceptors) {
        this.valuesToPropose = values;
        this.proposerId = proposerId;
        this.acceptors = acceptors;
    }

    @Override
    public void run() {
        for (int slot = 0; slot < valuesToPropose.length; slot++) {
            String value = valuesToPropose[slot];
            boolean success = propose(slot, value);
            if (success) {
                System.out.println("[Proposer] Proposal accepted in slot " + slot + ": " + value);
            } else {
                System.out.println("[Proposer] Proposal failed in slot " + slot);
            }
        }
    }

    private boolean propose(int slot, String value) {
        int proposalId = globalProposalId.incrementAndGet();
        int quorum = (acceptors.size() / 2) + 1;
        int prepareCount = 0;

        for (Acceptor acceptor : acceptors) {
            if (acceptor.prepare(proposalId)) {
                prepareCount++;
            }
        }

        if (prepareCount < quorum) {
            System.out.println("[Proposer] Prepare phase failed for slot " + slot);
            return false;
        }

        int acceptCount = 0;
        for (Acceptor acceptor : acceptors) {
            if (acceptor.accept(proposalId, value)) {
                acceptCount++;
            }
        }

        return acceptCount >= quorum;
    }

}
