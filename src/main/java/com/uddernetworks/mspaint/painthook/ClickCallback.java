package com.uddernetworks.mspaint.painthook;

import com.sun.jna.Callback;

import java.io.IOException;

@FunctionalInterface
public interface ClickCallback extends Callback {
    void onClick() throws IOException;
}
