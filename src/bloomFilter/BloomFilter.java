package bloomFilter;

import java.util.BitSet;
import java.util.List;
import java.util.function.Function;

public class BloomFilter<T> {

    private final BitSet bitSet;
    private final int bitSetSize;
    private final int numberOfHashFunctions;
    private final List<Function<T, Integer>> hashFunctions;

    public BloomFilter(int bitSetSize, List<Function<T, Integer>> hashFunctions) {

        if (bitSetSize <= 0) {
            throw new IllegalArgumentException("Bit set size must be positive.");
        }

        if (hashFunctions == null || hashFunctions.isEmpty()) {
            throw new IllegalArgumentException("At least one hash function is required.");
        }

        this.bitSetSize = bitSetSize;
        this.hashFunctions = hashFunctions;
        this.numberOfHashFunctions = hashFunctions.size();
        this.bitSet = new BitSet(bitSetSize);
    }

    public synchronized void add(T element) {

        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null.");
        }

        for (Function<T, Integer> hashFunction : hashFunctions) {
            int hash = Math.abs(hashFunction.apply(element) % bitSetSize);
            bitSet.set(hash);
        }

    }

    public boolean mightContain(T element) {

        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null.");
        }

        for (Function<T, Integer> hashFunction : hashFunctions) {
            int hash = Math.abs(hashFunction.apply(element) % bitSetSize);
            if (!bitSet.get(hash)) {
                return false;
            }
        }

        return true;
    }

    public int getBitSetSize() {
        return bitSetSize;
    }

    public int getNumberOfHashFunctions() {
        return numberOfHashFunctions;
    }

}
