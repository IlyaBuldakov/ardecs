package org.buldakov.model.cache.strategy;

import org.buldakov.model.cache.CacheMetaDataEntry;
import org.buldakov.model.cache.filesystem.L2CacheResolver;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

/**
 * Реализация стратегии с числовым приоритетом {@link Integer}.
 * Базово реализует LFU стратегию.
 */
public class IntegerPriorityCacheStrategy extends AbstractCacheStrategy<Integer> {

    public IntegerPriorityCacheStrategy(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Конструктор с {@link Comparator} для переопределения приоритета.
     *
     * @param initialCapacity Вместимость кэш-хранилища (проецируется на приоритетную очередь)
     * @param comparator {@link Comparator Компаратор}.
     */
    protected IntegerPriorityCacheStrategy(int initialCapacity, Comparator<CacheMetaDataEntry<Integer>> comparator) {
        super(initialCapacity, comparator);
    }

    @Override
    public void increaseCachePriority(String cacheKey, Object cacheValue) throws IOException, ClassNotFoundException {
        CacheMetaDataEntry<Integer> entry = this.removeEntryByKey(cacheKey);
        Integer oldPriorityValue = entry.getPriority();
        if (oldPriorityValue == null) oldPriorityValue = 0;
        entry.setPriority(++oldPriorityValue);
        this.cachePriorityQueue.add(entry);
        this.resolveInputDataToL2Cache(entry, cacheValue);
    }

    @Override
    public boolean contains(String cacheKey) {
        return this.cachePriorityQueue.contains(new CacheMetaDataEntry<>(cacheKey, 0));
    }

    @Override
    protected boolean resolvePriorityByAvg(CacheMetaDataEntry<Integer> entry, Object cacheValue) throws IOException {
        if (this.l2CacheResolver.isPresent()) {
            Optional<Integer> sum = this.cachePriorityQueue.stream().map(CacheMetaDataEntry::getPriority).reduce(Integer::sum);
            int avg = sum.map(integer -> integer / this.cachePriorityQueue.size()).orElse(0);
            if (entry.getPriority() >= avg) {
                L2CacheResolver l2CacheResolver = this.l2CacheResolver.get();
                l2CacheResolver.writeData(entry.getKey(), cacheValue);
                return true;
            }
        }
        return false;
    }

    @Override
    public void addPriorityEntry(String key) {
        CacheMetaDataEntry<Integer> cacheMetaDataEntry = new CacheMetaDataEntry<>(key, 1);
        this.cachePriorityQueue.add(cacheMetaDataEntry);
    }

}
