package com.uddernetworks.mspaint.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SocketServer implements Runnable {

    private static Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);

    private Socket socket;
    private InternalConnection internalConnection;
    private final Queue<Map.Entry<String, Object>> sendingData = new LinkedList<>();

    public SocketServer(Socket socket, Map<String, Object> initialData, InternalConnection internalConnection) {
        this.socket = socket;
        this.internalConnection = internalConnection;
        initialData.forEach((name, data) -> sendingData.add(new AbstractMap.SimpleEntry<>(name, data)));
    }

    @Override
    public void run() {
        this.internalConnection.setPort(this.socket.getPort());
        this.internalConnection.onConnect();
        try {
            var in = new Scanner(socket.getInputStream());
            var out = new PrintWriter(socket.getOutputStream(), true);

            CompletableFuture.runAsync(() -> {
                while (true) {
                    synchronized (this.sendingData) {
                        var entry = this.sendingData.poll();
                        if (entry == null) continue;
                        synchronized (out) {
                            out.println(entry.getKey() + "," + entry.getValue());
                        }
                    }
                }
            });

            while (true) {
                while (in.hasNextLine()) {
                    synchronized (out) {
                        var clientInput = in.nextLine();
                        var commaSplit = clientInput.split(",");
                        if (commaSplit.length != 2) continue;
                        out.println(this.internalConnection.accept(commaSplit[0], commaSplit[1]));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error during socket connection", e);
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    socket.close();
                    System.out.println("The server has shut down!");
                } catch (IOException e) {}
            }));
        }
    }
}
