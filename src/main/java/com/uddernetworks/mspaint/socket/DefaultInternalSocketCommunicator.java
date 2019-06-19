package com.uddernetworks.mspaint.socket;

import com.uddernetworks.mspaint.main.MainGUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class DefaultInternalSocketCommunicator implements InternalSocketCommunicator {

    private static Logger LOGGER = LoggerFactory.getLogger(DefaultInternalSocketCommunicator.class);

    private int port = -1;
    private boolean serving = false;

    private static final File SOCKET_FILE = new File(MainGUI.APP_DATA, "socketData.properties");
    private ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void startServer(InternalConnection internalConnection) {
        startServer(Collections.emptyMap(), internalConnection);
    }

    @Override
    public void startServer(Map<String, Object> initialData, InternalConnection internalConnection) {
        this.serving = true;
        while (!available(this.port)) this.port = ThreadLocalRandom.current().nextInt(1000, 10000);

        try {
            var properties = new Properties();
            properties.setProperty("port", String.valueOf(this.port));
            properties.store(new FileOutputStream(SOCKET_FILE), "This is automatically written to by the IDE for cross-instance communicating, do not modify manually.");
        } catch (IOException e) {
            LOGGER.error("There was a problem writing the socket port to the file, will suppress the error, but problems may arise when starting another instance of the IDE for any reason.", e);
        }

        LOGGER.info("Creating server with port {}", this.port);
        CompletableFuture.runAsync(() -> {
            try (var listener = new ServerSocket(getPort())) {
                LOGGER.info("Server is running...");
                while (true) {
                    executor.execute(new SocketServer(listener.accept(), initialData, internalConnection));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean isServing() {
        return this.serving;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public boolean serverAvailable() {
        try {
            // This can not be a setting, as settings loaded via the SettingsManager are loaded with a bunch of other
            // stuff, and to keep overhead and startup times down, only the bare minimum is loaded
            if (!SOCKET_FILE.isFile()) return false;
            Properties properties = new Properties();
            properties.load(Files.newInputStream(SOCKET_FILE.toPath()));
            return !available((this.port = Integer.parseInt(properties.getOrDefault("port", -1).toString())));
        } catch (IOException e) {
            LOGGER.warn("There was a problem reading the socketData.properties file! Assuming no server is available...", e);
            return false;
        }
    }

    @Override
    public void connectToServer(InternalConnection internalConnection, Consumer<Throwable> connectionError) {
        this.serving = false;
        try (var socket = new Socket("localhost", getPort())) {
            new SocketServer(socket, Collections.emptyMap(), internalConnection).run();
        } catch (Throwable e) {
            connectionError.accept(e);
        }
    }


    /**
     * Checks to see if a specific port is available.
     *
     * @param port the port to check for availability
     */
    private boolean available(int port) {
        if (port < 0 || port > 30000) {
            return false;
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException ignored) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException ignored) {}
            }
        }

        return false;
    }

}
