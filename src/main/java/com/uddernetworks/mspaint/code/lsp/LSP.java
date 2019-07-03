package com.uddernetworks.mspaint.code.lsp;

public enum LSP {
    JAVA(true),
    PYTHON(false),
    GO(true),
    JS(false);

    private boolean workspace;

    LSP(boolean workspace) {
        this.workspace = workspace;
    }

    public boolean usesWorkspaces() {
        return workspace;
    }
}
