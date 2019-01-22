package com.uddernetworks.mspaint.painthook;

import com.sun.jna.Callback;

@FunctionalInterface
interface ClickCallback extends Callback {
    void onClick();
}
