package org.buldakov.model.cache;

/**
 * Класс-контейнер мета-данных кэш-элемента.
 * @param <PriorityType> Тип приоритета, исходя из которого
 *                      кэш-элементы будут сравниваться в {@link java.util.PriorityQueue приоритетной очереди}.
 */
public class CacheMetaDataEntry<PriorityType extends Comparable<PriorityType>>
        implements Comparable<CacheMetaDataEntry<PriorityType>> {

    /**
     * Ключ кэша.
     */
    private final String key;

    /**
     * Приоритет кэша.
     */
    private PriorityType priority;

    public CacheMetaDataEntry(String key, PriorityType priority) {
        this.key = key;
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CacheMetaDataEntry<?> that = (CacheMetaDataEntry<?>) o;

        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public String getKey() {
        return key;
    }

    public PriorityType getPriority() {
        return priority;
    }

    public void setPriority(PriorityType priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return this.key;
    }

    /**
     * Метод сравнения контейнеров мета-даных кэш-элемента по их приоритету.
     */
    @Override
    public int compareTo(CacheMetaDataEntry<PriorityType> o) {
       return this.priority.compareTo(o.getPriority());
    }
}
