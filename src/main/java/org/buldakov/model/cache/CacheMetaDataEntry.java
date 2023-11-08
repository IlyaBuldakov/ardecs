package org.buldakov.model.cache;

public class CacheMetaDataEntry<PriorityType extends Comparable<PriorityType>>
        implements Comparable<CacheMetaDataEntry<PriorityType>> {

    private final String key;

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

    @Override
    public int compareTo(CacheMetaDataEntry<PriorityType> o) {
       return this.priority.compareTo(o.getPriority());
    }
}
