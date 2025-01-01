package consistentHashing;

public record Node(String id, int weight) {

    @Override
    public String toString() {
        return id + "(Weight: " + weight + ")";
    }

}
