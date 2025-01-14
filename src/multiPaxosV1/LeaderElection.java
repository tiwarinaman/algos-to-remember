package multiPaxosV1;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LeaderElection {

    private List<Proposer> proposers;
    private AtomicBoolean leaderExists = new AtomicBoolean(false);
    private AtomicBoolean stopMonitoring = new AtomicBoolean(false);

    public LeaderElection(List<Proposer> proposers) {
        this.proposers = proposers;
    }

    public void startElection() {
        Proposer leader = proposers.stream()
                .max((p1, p2) -> Integer.compare(p1.nodeId, p2.nodeId))
                .orElse(null);

        if (leader != null) {
            leader.isLeader = true;
            leaderExists.set(true);
            System.out.println("Leader elected: Node " + leader.nodeId);
        }
    }

    public void monitorLeader() {
        new Thread(() -> {
            while (!stopMonitoring.get()) {
                try {
                    Thread.sleep(5000);
                    if (!leaderExists.get()) {
                        System.out.println("Leader missing. Starting re-election.");
                        startElection();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Monitoring stopped.");
        }).start();
    }

    public void stopMonitoring() {
        stopMonitoring.set(true);
    }

}
