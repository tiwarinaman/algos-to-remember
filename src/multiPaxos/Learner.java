package multiPaxos;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Learner {

    private final List<Acceptor> acceptors;

    public Learner(List<Acceptor> acceptors) {
        this.acceptors = acceptors;
    }

    public void learn(int slots) {
        for (int slot = 0; slot < slots; slot++) {
            Map<String, Integer> valueCount = new HashMap<>();

            for (Acceptor acceptor : acceptors) {
                String value = acceptor.getAcceptedValue();
                if (value != null) {
                    valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);
                }
            }

            String decidedValue = valueCount.entrySet().stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .orElse(null);

            if (decidedValue != null) {
                System.out.println("[Learner] Slot " + slot + " decided value: " + decidedValue);
            } else {
                System.out.println("[Learner] Slot " + slot + " has no decided value");
            }
        }
    }

}
