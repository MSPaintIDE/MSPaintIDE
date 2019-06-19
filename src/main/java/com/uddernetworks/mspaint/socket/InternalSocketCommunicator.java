package com.uddernetworks.mspaint.socket;

import java.util.Map;
import java.util.function.Consumer;

public interface InternalSocketCommunicator {

    void startServer(InternalConnection internalConnection);

    void startServer(Map<String, Object> initialData, InternalConnection internalConnection);

    boolean isServing();

    int getPort();


    boolean serverAvailable();

    void connectToServer(InternalConnection internalConnection, Consumer<Throwable> connectionError);
}
