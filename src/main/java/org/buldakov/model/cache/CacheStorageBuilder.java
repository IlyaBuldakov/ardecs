package org.buldakov.model.cache;

import org.buldakov.model.cache.enums.DeleteStrategyType;
import org.buldakov.model.cache.strategy.AbstractCacheStrategy;
import org.buldakov.model.cache.strategy.IntegerPriorityCacheStrategy;
import org.buldakov.model.cache.strategy.LruCacheStrategy;
import org.buldakov.model.cache.strategy.MfuCacheStrategy;

import java.io.IOException;
import java.util.HashMap;

public class CacheStorageBuilder {

    private HashMap<String, Object> keyValueStorage;

    private int capacity;

    private AbstractCacheStrategy<?> cacheStrategy;

    private boolean isL2CacheEnabled;

    public CacheStorageBuilder() {
        this.keyValueStorage = new HashMap<>();
        this.capacity = 16;
        this.cacheStrategy = new IntegerPriorityCacheStrategy(this.capacity);
    }

    public CacheStorageBuilder setKeyValueStorage(
            HashMap<String, Object> keyValueStorage) {
        this.keyValueStorage = keyValueStorage;
        return this;
    }

    public CacheStorageBuilder setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public CacheStorageBuilder setCacheStrategy(DeleteStrategyType deleteStrategyType) {
        switch (deleteStrategyType) {
            case LFU -> this.cacheStrategy = new IntegerPriorityCacheStrategy(this.capacity);
            case MFU -> this.cacheStrategy = new MfuCacheStrategy(this.capacity);
            case LRU -> this.cacheStrategy = new LruCacheStrategy(this.capacity);
        }
        return this;
    }

    public CacheStorageBuilder enableL2Cache() {
        this.isL2CacheEnabled = true;
        return this;
    }

    public HashMap<String, Object> getKeyValueStorage() {
        return this.keyValueStorage;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public AbstractCacheStrategy<?> getCacheStrategy() {
        return this.cacheStrategy;
    }

    public CacheStorage build() throws IOException {
        return new CacheStorage(this);
    }

    public boolean isL2CacheEnabled() {
        return isL2CacheEnabled;
    }
}
