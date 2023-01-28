package io.collective;

import java.time.Clock;

public class SimpleAgedCache {

    ExpirableEntry[] cache;
    int size;
    Clock clock;

    public SimpleAgedCache(Clock clock) {
        this();
        this.clock = clock;
    }

    public SimpleAgedCache() {
        cache = new ExpirableEntry[10];
        size = 0;
    }

    public void put(Object key, Object value, int retentionInMillis) {
        if (this.size == 10) {
            return;
        }
        ExpirableEntry newEntry = new ExpirableEntry((String) key, (String) value, retentionInMillis);
        cache[size] = newEntry;
        this.size++;
    }

    public boolean isEmpty() {
        removeExpiredEntries();
        return this.size == 0;
    }

    public int size() {
        removeExpiredEntries();
        return this.size;
    }

    public void removeExpiredEntries() {
        if (clock == null) return;
        ExpirableEntry[] cache1 = new ExpirableEntry[10];
        int count = 0;
        for (int i = 0; i < this.size; i++) {
            ExpirableEntry entry = cache[i];
            if (entry.retentionInMillis > clock.instant().getEpochSecond()) {
                cache1[count++] = entry;
            }
        }
        cache = cache1;
        size = count;
    }

    public Object get(Object key) {
        removeExpiredEntries();
        for (int i = 0; i < this.size; i++) {
            ExpirableEntry cacheEntry = cache[i];
            if (cacheEntry != null && cacheEntry.key.equals(key)) {
                return cacheEntry.value;
            }
        }
        return null;
    }

    class ExpirableEntry {
        String key;
        String value;
        long retentionInMillis;

        ExpirableEntry(String key, String value, int retentionInMillis) {
            this.key = key;
            this.value = value;
            long seconds = 0;
            if (clock != null) {
                seconds = clock.instant().getEpochSecond();
            }
            this.retentionInMillis = (retentionInMillis / 1000) + seconds;
        }

        public String getValue() {
            return this.value;
        }
    }
}