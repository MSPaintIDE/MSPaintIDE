package com.uddernetworks.mspaint.gui.kvselection;

@FunctionalInterface
public interface OldNewChange {
    void onChange(KVCell cell, String oldValue, String newValue);
}
