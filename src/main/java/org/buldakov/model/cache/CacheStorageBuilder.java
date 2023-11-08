package org.buldakov.model.cache;

import org.buldakov.model.cache.enums.DeleteStrategyType;
import org.buldakov.model.cache.strategy.AbstractCacheStrategy;
import org.buldakov.model.cache.strategy.IntegerPriorityCacheStrategy;
import org.buldakov.model.cache.strategy.LruCacheStrategy;
import org.buldakov.model.cache.strategy.MfuCacheStrategy;

import java.util.HashMap;

public class CacheStorageBuilder<CacheValue> {

    private HashMap<String, CacheValue> keyValueStorage;

    private int capacity;

    private AbstractCacheStrategy<?> cacheStrategy;

    public CacheStorageBuilder() {
        this.keyValueStorage = new HashMap<>();
        this.capacity = 16;
        this.cacheStrategy = new IntegerPriorityCacheStrategy();
    }

    public CacheStorageBuilder<CacheValue> setKeyValueStorage(
            HashMap<String, CacheValue> keyValueStorage) {
        this.keyValueStorage = keyValueStorage;
        return this;
    }

    public CacheStorageBuilder<CacheValue> setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public CacheStorageBuilder<CacheValue> setCacheStrategy(DeleteStrategyType deleteStrategyType) {
        switch (deleteStrategyType) {
            case LFU -> this.cacheStrategy = new IntegerPriorityCacheStrategy();
            case MFU -> this.cacheStrategy = new MfuCacheStrategy();
            case LRU -> this.cacheStrategy = new LruCacheStrategy();
        }
        return this;
    }

    public HashMap<String, CacheValue> getKeyValueStorage() {
        return this.keyValueStorage;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public AbstractCacheStrategy<?> getCacheStrategy() {
        return this.cacheStrategy;
    }

    public CacheStorage<CacheValue> build() {
        return new CacheStorage<>(this);
    }
}
