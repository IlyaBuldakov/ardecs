package org.buldakov.model.cache.strategy;

import org.buldakov.model.cache.CacheMetaDataEntry;
import org.buldakov.model.cache.filesystem.L2CacheResolver;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * Реализация стратегии LRU с временным приоритетом {@link Instant}.
 */
public class LruCacheStrategy extends AbstractCacheStrategy<Instant> {

    private static final Comparator<CacheMetaDataEntry<Instant>> LRU_COMPARATOR
            = (entry1, entry2) -> -(entry1.getPriority().compareTo(entry2.getPriority()));

    public LruCacheStrategy(int initialCapacity) {
        super(initialCapacity, LRU_COMPARATOR);
    }

    @Override
    public void increaseCachePriority(String cacheKey, Object cacheValue) throws IOException, ClassNotFoundException {
        CacheMetaDataEntry<Instant> entry = this.removeEntryByKey(cacheKey);
        entry.setPriority(Instant.now());
        this.cachePriorityQueue.add(entry);
        this.resolveInputDataToL2Cache(entry, cacheValue);
    }

    @Override
    public boolean contains(String cacheKey) {
        return this.cachePriorityQueue.contains(new CacheMetaDataEntry<>(cacheKey, Instant.now()));
    }

    @Override
    protected boolean resolvePriorityByAvg(CacheMetaDataEntry<Instant> entry, Object cacheValue) throws IOException {
        if (this.l2CacheResolver.isPresent()) {
            BigInteger sum = BigInteger.ZERO;
            List<Long> listOfMillis = this.cachePriorityQueue.stream().map((elem) -> elem.getPriority().toEpochMilli()).toList();
            for (Long millis : listOfMillis) {
                sum = sum.add(BigInteger.valueOf(millis));
            }
            BigInteger avg = sum.divide(BigInteger.valueOf(this.cachePriorityQueue.size()));
            if (entry.getPriority().toEpochMilli() >= avg.longValue()) {
                L2CacheResolver l2CacheResolver = this.l2CacheResolver.get();
                l2CacheResolver.writeData(entry.getKey(), cacheValue);
                return true;
            }
        }
        return false;
    }

    @Override
    public void addPriorityEntry(String key) {
        CacheMetaDataEntry<Instant> cacheMetaDataEntry = new CacheMetaDataEntry<>(key, Instant.now());
        this.cachePriorityQueue.add(cacheMetaDataEntry);
    }
}
