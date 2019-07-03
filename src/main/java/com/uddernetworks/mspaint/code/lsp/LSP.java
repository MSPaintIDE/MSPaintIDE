package com.uddernetworks.mspaint.code.lsp;

public enum LSP {
    JAVA(true),
    PYTHON(false),
    GO(true);

    private boolean workspace;

    LSP(boolean workspace) {
        this.workspace = workspace;
    }

    public boolean usesWorkspaces() {
        return workspace;
    }
}
