package hashMap;

public class Main {
    public static void main(String[] args) {
        ConcurrentHashMapCustom<String, Integer> map = new ConcurrentHashMapCustom<>();

        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        System.out.println(map);

    }
}
