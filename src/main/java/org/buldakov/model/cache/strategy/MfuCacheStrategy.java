package org.buldakov.model.cache.strategy;

import org.buldakov.model.cache.CacheMetaDataEntry;

import java.util.Comparator;

/**
 * Реализация стратегии MFU.
 */
public class MfuCacheStrategy extends IntegerPriorityCacheStrategy {

    /**
     * {@link Comparator Компаратор} с обратным приоритетом в очереди.
     */
    private static final Comparator<CacheMetaDataEntry<Integer>> MFU_COMPARATOR
            = (entry1, entry2) -> -(entry1.getPriority().compareTo(entry2.getPriority()));

    public MfuCacheStrategy(int initialCapacity) {
        super(initialCapacity, MFU_COMPARATOR);
    }
}