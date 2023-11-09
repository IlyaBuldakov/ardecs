package org.buldakov.model.cache.enums;

/**
 * Перечисление стратегий вытеснения данных из кэша.
 */
public enum DeleteStrategyType {
    /**
     * Least Frequently Used.
     */
    LFU,

    /**
     * Most Frequently Used.
     */
    MFU,

    /**
     * Last Recently Used.
     */
    LRU
}
