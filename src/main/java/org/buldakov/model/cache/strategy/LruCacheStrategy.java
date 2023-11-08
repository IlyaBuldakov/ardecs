package org.buldakov.model.cache.strategy;

import org.buldakov.model.cache.CacheMetaDataEntry;

import java.time.Instant;

public class LruCacheStrategy extends AbstractCacheStrategy<Instant> {

    @Override
    public void increaseCachePriority(String cacheKey) {
        CacheMetaDataEntry<Instant> entry = this.removeEntryByKey(cacheKey);
        entry.setPriority(Instant.now());
        this.cachePriorityQueue.add(entry);
    }

    @Override
    public void addPriorityEntry(String key) {
        CacheMetaDataEntry<Instant> cacheMetaDataEntry = new CacheMetaDataEntry<>(key, Instant.now());
        this.cachePriorityQueue.add(cacheMetaDataEntry);
    }
}
