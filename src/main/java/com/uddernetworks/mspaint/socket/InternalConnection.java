package com.uddernetworks.mspaint.socket;

public abstract class InternalConnection {
    private int port = -1;

    public abstract void onConnect();

    public abstract String accept(String s, String s2);

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
