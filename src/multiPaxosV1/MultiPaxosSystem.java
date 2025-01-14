package multiPaxosV1;

import java.util.ArrayList;
import java.util.List;

public class MultiPaxosSystem {
    public static void main(String[] args) {
        // Create acceptors
        List<Acceptor> acceptors = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            acceptors.add(new Acceptor(i));
        }

        // Create proposers
        List<Proposer> proposers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            proposers.add(new Proposer(i, acceptors));
        }

        // Leader election
        LeaderElection leaderElection = new LeaderElection(proposers);
        leaderElection.startElection();
        leaderElection.monitorLeader();

        // Simulate proposals
        new Thread(() -> proposers.get(0).propose("Value A")).start();
        new Thread(() -> proposers.get(1).propose("Value B")).start();

        // Allow the system to run for a fixed duration
        try {
            Thread.sleep(15000); // Run for 15 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Stop leader monitoring and exit
        leaderElection.stopMonitoring();
        System.out.println("Program completed. Exiting...");
    }
}
