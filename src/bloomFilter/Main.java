package bloomFilter;

import java.util.List;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {

        // create hash functions
        Function<String, Integer> hashFunction1 = String::hashCode;
        Function<String, Integer> hashFunction2 = s -> (s.hashCode() * 31) ^ (s.hashCode() >>> 16);

        // create bloom filter
        BloomFilter<String> bloomFilter = new BloomFilter<>(
                1000,
                List.of(hashFunction1, hashFunction2)
        );

        // add elements
        bloomFilter.add("apple");
        bloomFilter.add("banana");

        // test elements
        System.out.println("apple: " + bloomFilter.mightContain("apple"));  // True
        System.out.println("banana: " + bloomFilter.mightContain("banana")); // True
        System.out.println("cherry: " + bloomFilter.mightContain("cherry")); // False or True (false positive possible)
    }
}
