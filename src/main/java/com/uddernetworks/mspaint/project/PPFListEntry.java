package com.uddernetworks.mspaint.project;

public class PPFListEntry<K, V> {
    private K key;
    private V value;

    public PPFListEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
