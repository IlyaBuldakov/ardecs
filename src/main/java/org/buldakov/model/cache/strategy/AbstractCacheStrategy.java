package org.buldakov.model.cache.strategy;

import org.buldakov.model.cache.CacheMetaDataEntry;

import java.util.Comparator;
import java.util.PriorityQueue;

public abstract class AbstractCacheStrategy<PriorityType extends Comparable<PriorityType>> {

    protected PriorityQueue<CacheMetaDataEntry<PriorityType>> cachePriorityQueue;

    protected AbstractCacheStrategy() {
        this.cachePriorityQueue = new PriorityQueue<>();
    }

    protected AbstractCacheStrategy(Comparator<CacheMetaDataEntry<PriorityType>> comparator) {
        this.cachePriorityQueue = new PriorityQueue<>(comparator);
    }

    public abstract void increaseCachePriority(String cacheKey);

    public CacheMetaDataEntry<PriorityType> removeEntry() {
        return this.cachePriorityQueue.remove();
    }

    public CacheMetaDataEntry<PriorityType> removeEntryByKey(String cacheKey) {
        CacheMetaDataEntry<PriorityType> entryToRemove
                = this.cachePriorityQueue.stream().filter((entry) -> entry.getKey().equals(cacheKey)).toList().get(0);
        this.cachePriorityQueue.remove(entryToRemove);
        return entryToRemove;
    }

    public abstract void addPriorityEntry(String key);
}