package hashMap;

import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentHashMapCustom<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Node<K, V>[] table;
    private final ReentrantLock[] locks;
    private int size;

    @SuppressWarnings("unchecked")
    public ConcurrentHashMapCustom(int capacity) {
        int cap = Math.max(1, capacity);
        table = (Node<K, V>[]) new Node[cap];
        locks = new ReentrantLock[cap];
        for (int i = 0; i < cap; i++) {
            locks[i] = new ReentrantLock();
        }
        size = 0;
    }

    public ConcurrentHashMapCustom() {
        this(DEFAULT_CAPACITY);
    }

    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private int hash(K key) {
        return (key.hashCode() & 0x7FFFFFFF) % table.length;
    }

    public V put(K key, V value) {
        if (key == null || value == null) throw new NullPointerException("Key or Value cannot be null");

        int index = hash(key);
        locks[index].lock();
        try {
            Node<K, V> current = table[index];
            while (current != null) {
                if (current.key.equals(key)) {
                    V oldValue = current.value;
                    current.value = value;
                    return oldValue;
                }
                current = current.next;
            }
            Node<K, V> newNode = new Node<>(key, value);
            newNode.next = table[index];
            table[index] = newNode;
            size++;

            if (size > LOAD_FACTOR * table.length) {
                resize();
            }
        } finally {
            locks[index].unlock();
        }
        return null;
    }

    public V get(K key) {
        int index = hash(key);
        locks[index].lock();
        try {
            Node<K, V> current = table[index];
            while (current != null) {
                if (current.key.equals(key)) {
                    return current.value;
                }
                current = current.next;
            }
        } finally {
            locks[index].unlock();
        }
        return null;
    }

    public V remove(K key) {
        int index = hash(key);
        locks[index].lock();
        try {
            Node<K, V> current = table[index];
            Node<K, V> prev = null;
            while (current != null) {
                if (current.key.equals(key)) {
                    if (prev == null) {
                        table[index] = current.next;
                    } else {
                        prev.next = current.next;
                    }
                    size--;
                    return current.value;
                }
                prev = current;
                current = current.next;
            }
        } finally {
            locks[index].unlock();
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public void clear() {
        for (int i = 0; i < table.length; i++) {
            locks[i].lock();
            try {
                table[i] = null;
            } finally {
                locks[i].unlock();
            }
        }
        size = 0;
    }

    public int size() {
        return this.size;
    }

    private void resize() {
        int newCapacity = table.length * 2;
        @SuppressWarnings("unchecked")
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCapacity];
        ReentrantLock[] newLocks = new ReentrantLock[newCapacity];
        for (int i = 0; i < newCapacity; i++) {
            newLocks[i] = new ReentrantLock();
        }

        for (int i = 0; i < table.length; i++) {
            locks[i].lock();
            try {
                Node<K, V> current = table[i];
                while (current != null) {
                    Node<K, V> next = current.next;
                    int newIndex = (current.key.hashCode() & 0x7FFFFFFF) % newCapacity;
                    current.next = newTable[newIndex];
                    newTable[newIndex] = current;
                    current = next;
                }
            } finally {
                locks[i].unlock();
            }
        }

        table = newTable;
        System.arraycopy(newLocks, 0, locks, 0, newLocks.length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean isFirst = true;

        for (int i = 0; i < table.length; i++) {
            locks[i].lock();
            try {
                Node<K, V> current = table[i];
                while (current != null) {
                    if (!isFirst) {
                        sb.append(", ");
                    }
                    sb.append(current.key).append("=").append(current.value);
                    isFirst = false;
                    current = current.next;
                }
            } finally {
                locks[i].unlock();
            }
        }

        sb.append("}");
        return sb.toString();
    }
}
