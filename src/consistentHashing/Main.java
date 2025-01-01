package consistentHashing;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        HashFunction hashFunction = new MD5HashFunction();
        ConsistentHashing consistentHashing = new ConsistentHashing(100, hashFunction);

        Node node1 = new Node("Node1", 1);
        Node node2 = new Node("Node2", 2);
        Node node3 = new Node("Node3", 1);

        consistentHashing.addNode(node1);
        consistentHashing.addNode(node2);
        consistentHashing.addNode(node3);

        consistentHashing.printRing();

        List<String> keys = Arrays.asList("Key1", "Key2", "Key3", "Key4", "Key5", "Key6", "Key7");
        for (String key : keys) {
            System.out.println(key + " is mapped to " + consistentHashing.getNode(key));
        }

        System.out.println("\nLoad distribution:");
        Map<String, Long> distribution = consistentHashing.calculateLoadDistribution(keys);
        distribution.forEach((node, count) -> System.out.println(node + ": " + count + " keys"));

        System.out.println("\nAdding Node4...");
        Node node4 = new Node("Node4", 1);
        consistentHashing.addNode(node4);

        consistentHashing.printRing();

        System.out.println("\nLoad distribution after adding Node4:");
        distribution = consistentHashing.calculateLoadDistribution(keys);
        distribution.forEach((node, count) -> System.out.println(node + ": " + count + " keys"));

        System.out.println("\nRemoving Node2...");
        consistentHashing.removeNode(node2);

        consistentHashing.printRing();

        System.out.println("\nLoad distribution after removing Node2:");
        distribution = consistentHashing.calculateLoadDistribution(keys);
        distribution.forEach((node, count) -> System.out.println(node + ": " + count + " keys"));
    }
}
