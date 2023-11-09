package org.buldakov.model.cache.strategy;

import org.buldakov.model.cache.CacheMetaDataEntry;
import org.buldakov.model.cache.filesystem.L2CacheResolver;

import java.io.IOException;
import java.util.*;

/**
 * Абстрактный класс стратегии.
 * <p>
 * Стратегия решает как должен быть реализован кэш, сортирует его по нужному приоритету,
 * ведёт учёт (добавление/удаление) кэша в своей {@link PriorityQueue очереди}.
 *
 * @param <PriorityType> Тип приоритета. Должен иметь возможность к сравнению для
 *                       распределения приоритета в очереди. Указывается в конкретной реализации стратегии.
 */
public abstract class AbstractCacheStrategy<PriorityType extends Comparable<PriorityType>> {

    /**
     * Разделитель ключа и значения в паре данных из L2 кэша.
     */
    private static final String KEY_VALUE_SEPARATOR = ":";

    /**
     * Приоритетная очередь кэша.
     */
    protected PriorityQueue<CacheMetaDataEntry<PriorityType>> cachePriorityQueue;

    /**
     * Множество ключей, полученных из L2 кэш файла.
     */
    protected HashSet<String> l2cacheKeysSet = new HashSet<>();

    /**
     * Опиционально: обработчик L2 кэша.
     * <p>
     * Пусто - если в конфигурации отключен L2 кэш.
     * Содержится объект в обертке {@link Optional} - если в конфигурации включен L2 кэш.
     */
    protected Optional<L2CacheResolver> l2CacheResolver = Optional.empty();

    /**
     * Вместимость главного кэш-хранилища.
     * Проецируется на приоритетную очередь.
     */
    private final int initialCapacity;

    protected AbstractCacheStrategy(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        this.cachePriorityQueue = new PriorityQueue<>();
    }

    protected AbstractCacheStrategy(int initialCapacity, Comparator<CacheMetaDataEntry<PriorityType>> comparator) {
        this.initialCapacity = initialCapacity;
        this.cachePriorityQueue = new PriorityQueue<>(comparator);
    }

    /**
     * Метод увеличения приоритета в очереди конкретного кэш-элемента.
     * Его реализуют дочерние классы-стратегии, так как в зависимости от типа
     * приоритета механизмы будут разные.
     *
     * @param cacheKey   Ключ кэша.
     * @param cacheValue Значение кэша.
     * @throws IOException            Exception.
     * @throws ClassNotFoundException Exception.
     */
    public abstract void increaseCachePriority(String cacheKey, Object cacheValue) throws IOException, ClassNotFoundException;

    /**
     * Метод включения L2 кэша.
     * При включении из L2 кэш-файла вытягиваются данные и помещаются
     * в L1 кэш-хранилище для быстрого доступа.
     *
     * @return Кэш-элементы из L2 кэш-файла.
     * @throws IOException В случае неудачной инициализации кэш-файла.
     */
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

    /**
     * Метод получения данных из {@link L2CacheResolver}.
     *
     * @return Массив строк, где строка - пара ключа и значения, отформатированная
     * согласно константе формата.
     */
    public String[] getL2Cache() {
        if (this.l2CacheResolver.isPresent()) {
            try {
                String[] l2cacheData = this.l2CacheResolver.get().readData();
                return l2cacheData.length == 0 ? new String[]{} : l2cacheData;
            } catch (IOException exception) {
                System.err.println(exception.getMessage());
            }
        }
        return new String[]{};
    }

    /**
     * Метод заполнения очереди приоритетов из L2 кэша.
     *
     * @param l2cacheData Массив строк, где строка - пара ключа и значения, отформатированная
     *                    согласно константе формата.
     */
    private void fillPriorityQueueFromL2(String[] l2cacheData) {
        Arrays.stream(l2cacheData)
                .forEach((keyValueString)
                        -> {
                    if (this.cachePriorityQueue.size() != this.initialCapacity) {
                        String[] keyValueSplit = keyValueString.split(KEY_VALUE_SEPARATOR);
                        if (keyValueSplit.length == 2) this.addPriorityEntry(keyValueSplit[0]);
                    }
                });
        System.out.println("L2 Cache log: в приоритетную очередь кэша были добавлены элемент. Хранилище: : " + this.cachePriorityQueue);
    }

    /**
     * Метод удаления кэш-элемента из приоритетной очереди.
     *
     * @return Entry с мета-данными кэша.
     */
    public CacheMetaDataEntry<PriorityType> removeEntry() {
        return this.cachePriorityQueue.remove();
    }

    /**
     * Метод удаления кэш-элемента из приоритетной очереди по кэш-ключу.
     *
     * @return Entry с мета-данными кэша.
     */
    public CacheMetaDataEntry<PriorityType> removeEntryByKey(String cacheKey) {
        CacheMetaDataEntry<PriorityType> entryToRemove
                = this.cachePriorityQueue.stream().filter((entry) -> entry.getKey().equals(cacheKey)).toList().get(0);
        this.cachePriorityQueue.remove(entryToRemove);
        return entryToRemove;
    }

    /**
     * Метод отчистки приоритетной очереди.
     */
    public void clear() {
        this.cachePriorityQueue.clear();
    }

    /**
     * Метод проверки на факт того, содержит ли приоритетная очередь кэша
     * элемент по кэш-ключу.
     *
     * @param cacheKey Ключ кэша.
     * @return True/false.
     */
    public abstract boolean contains(String cacheKey);

    /**
     * Метод добавления в приоритетную очередь кэша.
     *
     * @param cacheKey Ключ кэша.
     */
    public abstract void addPriorityEntry(String cacheKey);

    /**
     * Метод, который добавляет ключ кэша, номинируемого на добавление в
     * L2 кэш в множество ключей кэша.
     *
     * Затем передаёт данные в метод обработки среднего значения.
     *
     * TODO: [FIX] Добавление в множество происходит до того, как произойдет проверка среднего значения. Исправить.
     *
     * @param entry Entry с мета-данными кэша.
     * @param cacheValue Значение кэша для сохранения в L2 кэш-файл.
     * @throws IOException Exception.
     * @throws ClassNotFoundException Exception.
     */
    protected void resolveInputDataToL2Cache(CacheMetaDataEntry<PriorityType> entry, Object cacheValue)
            throws IOException, ClassNotFoundException {
        String cacheKey = entry.getKey();
        if (!this.l2cacheKeysSet.contains(cacheKey)) {
            this.l2cacheKeysSet.add(entry.getKey());
            this.resolveL2Cache(entry, cacheValue);
        }
    }

    /**
     * Метод проверки среднего значения для решения - заносить кэш в L2 или нет.
     *
     * @param entry Entry с мета-данными кэша.
     * @param cacheValue Значение кэша.
     * @throws IOException Exception.
     * @throws ClassNotFoundException Exception.
     */
    protected abstract void resolveL2Cache(CacheMetaDataEntry<PriorityType> entry, Object cacheValue)
            throws IOException, ClassNotFoundException;

    /**
     * Метод инициализации множества ключей кэша, хранящихся в L2 кэш-файле.
     */
    private void initL2CacheKeysSet() {
        if (this.l2CacheResolver.isPresent()) {
            String[] l2cacheData = this.getL2Cache();
            Arrays.stream(l2cacheData).forEach((keyValuePair) -> {
                String[] keyValueSplit = keyValuePair.split(KEY_VALUE_SEPARATOR);
                if (keyValueSplit.length == 2) {
                    this.l2cacheKeysSet.add(keyValueSplit[0]);
                }
            });
        }
    }
}