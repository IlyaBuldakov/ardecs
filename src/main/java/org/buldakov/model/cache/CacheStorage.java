package org.buldakov.model.cache;

import org.buldakov.model.cache.strategy.AbstractCacheStrategy;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class CacheStorage {

    private final HashMap<String, Object> keyValueStorage;

    private final int capacity;

    private final AbstractCacheStrategy<?> cacheStrategy;

    CacheStorage(CacheStorageBuilder builder) throws IOException {
        this.keyValueStorage = builder.getKeyValueStorage();
        this.capacity = builder.getCapacity();
        this.cacheStrategy = builder.getCacheStrategy();
        if (builder.isL2CacheEnabled()) {
            String[] l2cacheData = this.cacheStrategy.enableL2Cache();
            if (l2cacheData.length != 0) {
                Arrays.stream(l2cacheData).forEach((data) -> {
                    if (data.length() > 0) {
                        if (this.keyValueStorage.size() != this.capacity) {
                            String[] dataSplit = data.split(":");
                            this.keyValueStorage.put(dataSplit[0], dataSplit[1]);
                        }
                    }
                });
            }
        }
    }

    public void addData(String cacheKey, Object value) {
        if (this.keyValueStorage.size() == capacity) {
            System.out.println("Cache log: попытка добавить элемент " + cacheKey + ". Хранилище переполнено. Хранилище: " + this.keyValueStorage);
            this.keyValueStorage.remove(this.cacheStrategy.removeEntry().getKey());
            System.out.println("Cache log: элемент удален. Хранилище: " + this.keyValueStorage);
        }
        this.keyValueStorage.put(cacheKey, value);
        if (!this.cacheStrategy.contains(cacheKey)) {
            this.cacheStrategy.addPriorityEntry(cacheKey);
        }
        System.out.println("Cache log: добавлен новый элемент. Хранилище: " + this.keyValueStorage);
    }

    public Object getData(String cacheKey) throws IOException, ClassNotFoundException {
        System.out.println("Cache log: используется элемент " + cacheKey);
        Object cacheValue = this.keyValueStorage.get(cacheKey);
        this.cacheStrategy.increaseCachePriority(cacheKey, cacheValue);
        return cacheValue;
    }

    public void clear() {
        this.keyValueStorage.clear();
        this.cacheStrategy.clear();
    }
}
