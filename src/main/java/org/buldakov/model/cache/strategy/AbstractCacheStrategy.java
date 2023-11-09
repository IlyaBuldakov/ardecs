package org.buldakov.model.cache.strategy;

import org.buldakov.model.cache.CacheMetaDataEntry;
import org.buldakov.model.cache.filesystem.L2CacheResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;

public abstract class AbstractCacheStrategy<PriorityType extends Comparable<PriorityType>> {

    protected PriorityQueue<CacheMetaDataEntry<PriorityType>> cachePriorityQueue;

    protected HashSet<String> l2cacheKeysSet = new HashSet<>();

    protected Optional<L2CacheResolver> l2CacheResolver = Optional.empty();

    private final int initialCapacity;

    protected AbstractCacheStrategy(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        this.cachePriorityQueue = new PriorityQueue<>();
    }

    protected AbstractCacheStrategy(int initialCapacity, Comparator<CacheMetaDataEntry<PriorityType>> comparator) {
        this.initialCapacity = initialCapacity;
        this.cachePriorityQueue = new PriorityQueue<>(comparator);
    }

    public abstract void increaseCachePriority(String cacheKey, Object cacheValue) throws IOException, ClassNotFoundException;

    public String[] enableL2Cache() throws IOException {
        this.l2CacheResolver = Optional.of(new L2CacheResolver());
        System.out.println("L2 Cache log: второй уровень кэша активирован");
        this.initL2CacheKeysSet();
        String[] l2cacheData = this.getL2Cache();
        if (l2cacheData.length != 0) {
            this.fillPriorityQueueFromL2(l2cacheData);
            return l2cacheData;
        }
        return new String[]{};
    }

    public String[] getL2Cache() {
        try {
            String[] l2cacheData = this.l2CacheResolver.get().readData();
            return l2cacheData.length == 0 ? new String[]{} : l2cacheData;
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
        return new String[]{};
    }

    private void fillPriorityQueueFromL2(String[] l2cacheData) {
        Arrays.stream(l2cacheData)
                .forEach((keyValueString)
                        -> {
                    if (this.cachePriorityQueue.size() != this.initialCapacity) {
                        String[] keyValueSplit = keyValueString.split(":");
                        if (keyValueSplit.length == 2) this.addPriorityEntry(keyValueSplit[0]);
                    }
                });
        System.out.println("L2 Cache log: в приоритетную очередь кэша были добавлены элемент. Хранилище: : " + this.cachePriorityQueue);
    }

    public CacheMetaDataEntry<PriorityType> removeEntry() {
        return this.cachePriorityQueue.remove();
    }

    public CacheMetaDataEntry<PriorityType> removeEntryByKey(String cacheKey) {
        CacheMetaDataEntry<PriorityType> entryToRemove
                = this.cachePriorityQueue.stream().filter((entry) -> entry.getKey().equals(cacheKey)).toList().get(0);
        this.cachePriorityQueue.remove(entryToRemove);
        return entryToRemove;
    }

    public void clear() {
        this.cachePriorityQueue.clear();
    }

    public abstract boolean contains(String cacheKey);

    public abstract void addPriorityEntry(String key);

    protected void resolveInputDataToL2Cache(CacheMetaDataEntry<PriorityType> entry, Object cacheValue)
            throws IOException, ClassNotFoundException {
        String cacheKey = entry.getKey();
        if (!this.l2cacheKeysSet.contains(cacheKey)) {
            this.l2cacheKeysSet.add(entry.getKey());
            this.resolveL2Cache(entry, cacheValue);
        }
    }

    protected abstract void resolveL2Cache(CacheMetaDataEntry<PriorityType> entry, Object cacheValue)
            throws IOException, ClassNotFoundException;

    private void initL2CacheKeysSet() {
        if (this.l2CacheResolver.isPresent()) {
            String[] l2cacheData = this.getL2Cache();
            Arrays.stream(l2cacheData).forEach((keyValuePair) -> {
                String[] keyValueSplit = keyValuePair.split(":");
                if (keyValueSplit.length == 2) {
                    this.l2cacheKeysSet.add(keyValueSplit[0]);
                }
            });
        }
    }
}