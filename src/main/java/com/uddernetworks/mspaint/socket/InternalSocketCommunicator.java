package com.uddernetworks.mspaint.socket;

import java.util.Map;
import java.util.function.Consumer;

public interface InternalSocketCommunicator {

    /**
     * Starts the socket with the given {@link InternalConnection} without any initial data.
     *
     * @param internalConnection The {@link InternalConnection} to start the server with
     */
    void startServer(InternalConnection internalConnection);

    /**
     * Starts the socket with the given {@link InternalConnection} with the given initial data.
     *
     * @param initialData The initial key/value data to send to clients initially upon connecting
     * @param internalConnection The {@link InternalConnection} to start the server with
     */
    void startServer(Map<String, Object> initialData, InternalConnection internalConnection);

    /**
     * Gets if the server is actively serving.
     *
     * @return If the server is actively serving
     */
    boolean isServing();

    /**
     * Gets the port being used.
     *
     * @return The port being used
     */
    int getPort();

    /**
     * Tests if a server is available through the socket file's port.
     *
     * @return If a server is available and connectable
     */
    boolean serverAvailable();

    /**
     * Connects the current client to a server with the given {@link InternalConnection}.
     *
     * @param internalConnection The {@link InternalConnection} to connect with
     * @param connectionError If an exception/error occurs while connected at all
     */
    void connectToServer(InternalConnection internalConnection, Consumer<Throwable> connectionError);
}
