package org.buldakov.model.cache;

import org.buldakov.model.cache.strategy.AbstractCacheStrategy;

import java.util.HashMap;

public class CacheStorage<CacheValue> {

    private final HashMap<String, CacheValue> keyValueStorage;

    private final int capacity;

    private final AbstractCacheStrategy<?> cacheStrategy;

    CacheStorage(CacheStorageBuilder<CacheValue> builder) {
        this.keyValueStorage = builder.getKeyValueStorage();
        this.capacity = builder.getCapacity();;
        this.cacheStrategy = builder.getCacheStrategy();
    }

    public void addData(String cacheKey, CacheValue value) {
        if (this.keyValueStorage.size() == capacity) {
            System.out.println("Cache log: хранилище переполнено. Хранилище: " + this.keyValueStorage);
            this.keyValueStorage.remove(this.cacheStrategy.removeEntry().getKey());
            System.out.println("Cache log: элемент удален. Хранилище: " + this.keyValueStorage);
        }
        this.keyValueStorage.put(cacheKey, value);
        this.cacheStrategy.addPriorityEntry(cacheKey);
        System.out.println("Cache log: добавлен новый элемент. Хранилище: " + this.keyValueStorage);
    }

    public CacheValue getData(String cacheKey) {
        System.out.println("Cache log: используется элемент " + cacheKey);
        this.cacheStrategy.increaseCachePriority(cacheKey);
        return this.keyValueStorage.get(cacheKey);
    }
}
