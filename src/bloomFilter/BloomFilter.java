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

        if (bitSetSize <= 0 || bitSetSize > Integer.MAX_VALUE / 8) {
            throw new IllegalArgumentException("Bit set size must be positive and not exceed Integer.MAX_VALUE / 8.");
        }

        if (hashFunctions == null || hashFunctions.isEmpty()) {
            throw new IllegalArgumentException("At least one hash function is required.");
        }

        // Size of the bit set
        this.bitSetSize = bitSetSize;
        // List of hash functions
        this.hashFunctions = hashFunctions;
        // Number of hash functions
        this.numberOfHashFunctions = hashFunctions.size();
        // Bit set for storing element presence
        this.bitSet = new BitSet(bitSetSize);
    }

    public synchronized void add(T element) {

        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null.");
        }

        for (Function<T, Integer> hashFunction : hashFunctions) {
            int hash = (int) Long.remainderUnsigned(Math.abs((long) hashFunction.apply(element)), bitSetSize);
            bitSet.set(hash);
        }

    }

    public boolean mightContain(T element) {

        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null.");
        }

        for (Function<T, Integer> hashFunction : hashFunctions) {
            int hash = (int) Long.remainderUnsigned(Math.abs((long) hashFunction.apply(element)), bitSetSize);
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
