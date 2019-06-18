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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public abstract class ProcessStreamConnectionProvider {

    private Process process;
    private List<String> commands;
    private String workingDir;

    public ProcessStreamConnectionProvider() {
    }

    public ProcessStreamConnectionProvider(List<String> commands) {
        this.commands = commands;
    }

    public ProcessStreamConnectionProvider(List<String> commands, String workingDir) {
        this.commands = commands;
        this.workingDir = workingDir;
    }

    public void start() throws IOException {
        if (this.commands == null || this.commands.isEmpty() || this.commands.stream().anyMatch(Objects::isNull)) {
            throw new IOException("Unable to start language server: " + this);
        }

        if (!(this.process = createProcessBuilder().start()).isAlive()) {
            throw new IOException("Unable to start language server: " + this);
        }
    }

    protected ProcessBuilder createProcessBuilder() {
        ProcessBuilder builder = new ProcessBuilder(getCommands());
        if (getWorkingDirectory() != null) {
            builder.directory(new File(getWorkingDirectory()));
        }

        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        return builder;
    }

    public InputStream getInputStream() {
        return process == null ? null : process.getInputStream();
    }

    public InputStream getErrorStream() {
        return process == null ? null : process.getErrorStream();
    }

    public OutputStream getOutputStream() {
        return process == null ? null : process.getOutputStream();
    }

    public void stop() {
        if (process != null) process.destroy();
    }

    protected List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    protected String getWorkingDirectory() {
        return workingDir;
    }

    public void setWorkingDirectory(String workingDir) {
        this.workingDir = workingDir;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProcessStreamConnectionProvider)) return false;

        ProcessStreamConnectionProvider other = (ProcessStreamConnectionProvider) obj;
        return Objects.equals(this.getCommands(), other.getCommands())
                && Objects.equals(this.getWorkingDirectory(), other.getWorkingDirectory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getCommands(), this.getWorkingDirectory());
    }

    @Override
    public String toString() {
        return "ProcessStreamConnectionProvider [commands=" + this.getCommands() + ", workingDir=" + this.getWorkingDirectory() + "]";
    }

}
