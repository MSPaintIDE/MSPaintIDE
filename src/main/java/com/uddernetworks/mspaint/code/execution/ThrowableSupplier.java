package com.uddernetworks.mspaint.code.execution;

@FunctionalInterface
public interface ThrowableSupplier<T> {
    T get() throws Exception;
}
