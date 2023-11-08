package org.buldakov.model.cache.strategy;

import org.buldakov.model.cache.CacheMetaDataEntry;

import java.util.Comparator;

public class IntegerPriorityCacheStrategy extends AbstractCacheStrategy<Integer> {

    public IntegerPriorityCacheStrategy() {
    }

    protected IntegerPriorityCacheStrategy(Comparator<CacheMetaDataEntry<Integer>> comparator) {
        super(comparator);
    }

    @Override
    public void increaseCachePriority(String cacheKey) {
        CacheMetaDataEntry<Integer> entry = this.removeEntryByKey(cacheKey);
        Integer oldPriorityValue = entry.getPriority();
        if (oldPriorityValue == null) oldPriorityValue = 0;
        entry.setPriority(++oldPriorityValue);
        this.cachePriorityQueue.add(entry);
    }

    @Override
    public void addPriorityEntry(String key) {
        CacheMetaDataEntry<Integer> cacheMetaDataEntry = new CacheMetaDataEntry<>(key, 0);
        this.cachePriorityQueue.add(cacheMetaDataEntry);
    }
}
