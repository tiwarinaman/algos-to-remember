package multiPaxos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiPaxos {
    public static void main(String[] args) throws InterruptedException {
        int numAcceptors = 5;
        int numProposers = 2;
        int slots = 3;

        List<Acceptor> acceptors = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < numAcceptors; i++) {
            acceptors.add(new Acceptor());
        }

        String[][] proposerValues = {
                {"command1", "command2", "command3"},
                {"command4", "command5", "command6"}
        };

        ExecutorService executor = Executors.newFixedThreadPool(numProposers);
        for (int i = 0; i < numProposers; i++) {
            executor.submit(new Proposer(proposerValues[i], i, acceptors));
        }

        executor.shutdown();
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            System.err.println("[Main] Proposers did not finish in time.");
            executor.shutdownNow();
        }

        Learner learner = new Learner(acceptors);
        learner.learn(slots);
    }
}
