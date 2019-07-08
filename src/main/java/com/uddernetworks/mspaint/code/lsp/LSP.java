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

    /**
     * Gets if the LSP server uses workspaces.
     *
     * @return If the LSP server uses workspaces
     */
    public boolean usesWorkspaces() {
        return workspace;
    }
}
