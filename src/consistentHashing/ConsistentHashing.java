package consistentHashing;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public class ConsistentHashing {

    private final int numberOfReplicas; // Virtual nodes per physical node
    private final ConcurrentSkipListMap<Integer, Node> ring = new ConcurrentSkipListMap<>(); // Thread-safe ring representation
    private final Map<String, List<Integer>> nodeToHashes = new HashMap<>(); // Track virtual nodes for quick removal
    private final HashFunction hashFunction;

    public ConsistentHashing(int numberOfReplicas, HashFunction hashFunction) {
        if (numberOfReplicas <= 0) {
            throw new IllegalArgumentException("Number of replicas must be greater than zero.");
        }
        this.numberOfReplicas = numberOfReplicas;
        this.hashFunction = hashFunction;
    }

    public void addNode(Node node) {
        Objects.requireNonNull(node, "Node cannot be null.");
        List<Integer> hashes = new ArrayList<>();
        for (int i = 0; i < numberOfReplicas * node.weight(); i++) {
            String virtualNodeKey = node.id() + "#" + i;
            int hash = hashFunction.hash(virtualNodeKey);
            ring.put(hash, node);
            hashes.add(hash);
        }
        nodeToHashes.put(node.id(), hashes);
    }

    public void removeNode(Node node) {
        Objects.requireNonNull(node, "Node cannot be null.");
        List<Integer> hashes = nodeToHashes.get(node.id());
        if (hashes != null) {
            for (int hash : hashes) {
                ring.remove(hash);
            }
            nodeToHashes.remove(node.id());
        }
    }

    public Node getNode(String key) {
        if (ring.isEmpty()) {
            throw new IllegalStateException("No nodes are available in the ring.");
        }
        int hash = hashFunction.hash(key);
        Map.Entry<Integer, Node> entry = ring.ceilingEntry(hash);
        if (entry == null) {
            return ring.firstEntry().getValue();
        }
        return entry.getValue();
    }

    public void printRing() {
        System.out.println("Ring structure:");
        ring.forEach((hash, node) -> System.out.println(hash + " -> " + node));
    }

    public Map<String, Long> calculateLoadDistribution(List<String> keys) {
        return keys.stream()
                .map(this::getNode)
                .collect(Collectors.groupingBy(Node::id, Collectors.counting()));
    }

}
