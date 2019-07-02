package com.uddernetworks.mspaint.cmd;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamConsumer extends Thread {

    private InputStream inputStream;
    private Logger logger;
    private StringBuilder line = new StringBuilder();

    public InputStreamConsumer(InputStream inputStream, Logger logger) {
        this.inputStream = inputStream;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            int intVal;
            while ((intVal = inputStream.read()) != -1) {
                char value = (char) intVal;
                if (value == '\n') {
                    logger.info(line.toString());
                    line = new StringBuilder();
                } else if (value != '\r') {
                    line.append(value);
                }
            }
        } catch (IOException exp) {
            exp.printStackTrace();
        }

    }
}
