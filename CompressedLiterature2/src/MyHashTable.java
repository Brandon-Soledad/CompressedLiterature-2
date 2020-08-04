import java.util.HashSet;
import java.util.Set;

public class MyHashTable<K, V> {

	private final int capacity;

    private int size;

    private int maxProbe;

    private int totalProbe;

    private V getValue;

    private final Entry<K, V>[] entryList;

    private final Set<K> keys;

    private final int[] probeStat;

    public MyHashTable(final int capacity) {
        this.capacity = capacity;
        size = 0;
        maxProbe = 0;
        totalProbe = 0;
        entryList = new Entry[this.capacity];
        probeStat = new int[this.capacity];
        keys = new HashSet<K>();
    }

    public void put(final K searchKey, final V newValue) {
        final Entry<K, V> entry = new Entry<K, V>(searchKey, newValue);
        final int index = Math.abs(hash(searchKey));
        final int count = 0;
        if (entryList[index] == null) {
            entryList[index] = entry;
            size++;
            keys.add(searchKey);
        } else if (!entryList[index].getKey().equals(searchKey)) {
            keys.add(searchKey);
            if (index == capacity - 1) {
                probe(entry, 0, count);
            } else {
                probe(entry, index + 1, count);
            }
            size++;
        } else {
            entryList[index].setValue(newValue);
        }
    }
    private void probe(final Entry<K, V> entry, final int index, int count) {
        if (entryList[index] == null) {
            probeStat[count]++;
            totalProbe += count;
            maxProbe = Math.max(maxProbe, count);
            entryList[index] = entry;
        } else if (entryList[index].getKey().equals(entry.getKey())) {
            entryList[index].setValue(entry.getValue());
        } else if (index == capacity - 1) {
            count++;
            probe(entry, 0, count);
        } else {
            count++;
            probe(entry, index + 1, count);
        }
    }
    public V get(final K searchKey) {
        final int index = Math.abs(hash(searchKey));
        if (entryList[index] != null) {
            if (entryList[index].getKey().equals(searchKey)) {
                getValue = entryList[index].getValue();
            } else if (index == capacity - 1) {
                search(searchKey, 0);
            } else {
                search(searchKey, index + 1);
            }
        } else {
            System.out.println("Key does not exist!");
        }
        return getValue;
    }

    private void search(final K searchKey, final int index) {
        if (entryList[index] != null) {
            if (entryList[index].getKey().equals(searchKey)) {
                getValue = entryList[index].getValue();
            } else if (index == capacity - 1) {
                search(searchKey, 0);
            } else {
                search(searchKey, index + 1);
            }
        }
    }

    public boolean containsKey(final K searchKey) {
        boolean contain = false;
        final int index = Math.abs(hash(searchKey));

        final int count = 0;
        if (entryList[index] == null) {
            contain = false;
        } else if (entryList[index] != null && !entryList[index].getKey().equals(searchKey)) {
            if (index == capacity - 1) {
                contain = contains(searchKey, 0, count);
            } else {
                contain = contains(searchKey, index + 1, count);
            }
        } else {
            contain = true;
        }

        return contain;
    }

    private boolean contains(final K searchKey, final int index, final int count) {
        boolean contain;

        if (entryList[index] == null || count == capacity - 1) {
            contain = false;
        } else if (entryList[index].getKey().equals(searchKey)) {
            contain = true;
        } else if (index == capacity - 1) {
            contain = contains(searchKey, 0, count + 1);
        } else {
            contain = contains(searchKey, index + 1, count + 1);
        }
        return contain;
    }

    public Set<K> entrySet() {
        return keys;
    }

    public void stats() {
        int probe = 0;
        int countEmpty = 0;
        final StringBuilder sb = new StringBuilder();
        sb.append("Hash Table Stats\n");
        sb.append("================\n");
        sb.append(String.format("Number of Entries: %d \n", size));
        sb.append(String.format("Number of Buckets: %d \n", capacity));
        sb.append("Histogram of Probes: \n");
        sb.append("[" + probeStat[0]);
        for (int i = 1; i < maxProbe; i++) {
            if (probeStat[i] == 0) {
                countEmpty++;
            }
            probe += probeStat[i];
            sb.append(", " + probeStat[i]);
        }
        sb.append("] \n");
        final double percent = (((double) maxProbe - countEmpty) / maxProbe) * 100;
        sb.append(String.format("Fill Percentage: %f %%\n", percent));
        sb.append(String.format("Max Linear Probe: %d \n", maxProbe));
        sb.append(String.format("Average Linear Probe: %f \n", ((double) totalProbe / probe)));
        System.out.println(sb);
    }

    public int size() {
        return size;
    }

    private int hash(final K key) {
        return key.hashCode() % ((capacity / 2) + (capacity / 2));
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < capacity - 1; i++) {
            sb.append("Key: " + entryList[i].getKey() + " || ");
            sb.append("Value: " + entryList[i].getValue() + "\n");
        }
        return sb.toString();
    }
    private static class Entry<K, V> {

        final K key;
        V value;

        public Entry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(final V value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "(" + key + "," + value + ")";
		}
	}
}