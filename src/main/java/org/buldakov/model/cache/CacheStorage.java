package org.buldakov.model.cache;

import org.buldakov.model.cache.strategy.AbstractCacheStrategy;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Главный класс кэш-хранилища.
 * Использует {@link HashMap} для быстрого доступа к данным типа "ключ-значение".
 */
public class CacheStorage {

    /**
     * Разделитель ключа и значения в паре данных из L2 кэша.
     */
    private static final String KEY_VALUE_SEPARATOR = ":";

    /**
     * L1 кэш-хранилище типа "ключ-значение".
     * Использует {@link String} в качестве ключа для
     * достижения наибольшей производительности.
     */
    private final HashMap<String, Object> keyValueStorage;

    /**
     * Вместимость L1 кэша.
     */
    private final int capacity;

    /**
     * Стратегия, которую использует кэш-хранилище.
     */
    private final AbstractCacheStrategy<?> cacheStrategy;

    /**
     * Конструктор создания кэш-хранилища из {@link CacheStorageBuilder билдера}.
     * При создании заносит в свой {@link HashMap} данные из L2 кэш-файла.
     *
     * @param builder Билдер кэш-хранилища.
     * @throws IOException В случае неудачной активации L2 кэша.
     */
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
                            String[] dataSplit = data.split(KEY_VALUE_SEPARATOR);
                            this.keyValueStorage.put(dataSplit[0], dataSplit[1]);
                        }
                    }
                });
            }
        }
    }

    /**
     * Метод добавления данных в хранилище.
     * Проверяет, есть ли место в хранилище и удаляет элементы
     * исходя из стратегии если их некуда записать.
     *
     * @param cacheKey Ключ кэша.
     * @param value Значение кэша.
     */
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

    /**
     * Метод получения данных из кэш хранилища.
     * Увеличивает приоритет в приоритетной очереди кэша, так как элемент был использован.
     *
     * @param cacheKey Ключ кэша.
     * @return Данные по ключу кэша.
     * @throws IOException Exception.
     * @throws ClassNotFoundException Exception.
     */
    public Object getData(String cacheKey) throws IOException, ClassNotFoundException {
        System.out.println("Cache log: используется элемент " + cacheKey);
        Object cacheValue = this.keyValueStorage.get(cacheKey);
        this.cacheStrategy.increaseCachePriority(cacheKey, cacheValue);
        return cacheValue;
    }

    /**
     * Метод отчистки L1 кэш-хранилища.
     */
    public void clear() {
        this.keyValueStorage.clear();
        this.cacheStrategy.clear();
    }
}
