/*
 * Modified for MS Paint IDE by Adam Yarris from LSP4E (https://git.eclipse.org/c/lsp4e/lsp4e.git/about/)
 * Original header:
 *
 * /*******************************************************************************
 * Copyright (c) 2019 Rogue Wave Software Inc. and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Michał Niewrzał (Rogue Wave Software Inc.) - initial implementation
 *  Pierre-Yves B. <pyvesdev@gmail.com> - Bug 545950 - Specifying the directory in ProcessStreamConnectionProvider should not be mandatory
 *  Pierre-Yves B. <pyvesdev@gmail.com> - Bug 508812 - Improve error and logging handling
 *******************************************************************************/

package com.uddernetworks.mspaint.code.lsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class ProcessOverSocketStreamConnectionProvider extends ProcessStreamConnectionProvider {

    private static Logger LOGGER = LoggerFactory.getLogger(ProcessOverSocketStreamConnectionProvider.class);

    private int port;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ProcessOverSocketStreamConnectionProvider(List<String> commands, int port) {
        super(commands);
        this.port = port;
    }

    public ProcessOverSocketStreamConnectionProvider(List<String> commands, String workingDir, int port) {
        super(commands, workingDir);
        this.port = port;
    }

    @Override
    public void start() throws IOException {
        var processBuilder = createProcessBuilder();
        LOGGER.info("Starting server process with commands");
        var process = processBuilder.start();
        if (!process.isAlive()) {
            throw new IOException("Unable to start language server: " + this);
        } else {
            LOGGER.info("Server process started {}", process);
        }

        if (socket == null) {
            throw new IOException("Unable to make socket connection: " + this);
        }

        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void stop() {
        super.stop();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.error("There was an exception during stop()", e);
            }
        }
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        return result ^ Objects.hashCode(this.port);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProcessOverSocketStreamConnectionProvider)) return false;
        if (obj == this) return true;

        ProcessOverSocketStreamConnectionProvider other = (ProcessOverSocketStreamConnectionProvider) obj;
        return Objects.equals(this.getCommands(), other.getCommands())
                && Objects.equals(this.getWorkingDirectory(), other.getWorkingDirectory())
                && Objects.equals(this.socket, other.socket);
    }

    @Override
    public String toString() {
        return "ProcessOverSocketStreamConnectionProvider [socket=" + socket + ", commands=" + this.getCommands()
                + ", workingDir=" + this.getWorkingDirectory() + "]";
    }

}