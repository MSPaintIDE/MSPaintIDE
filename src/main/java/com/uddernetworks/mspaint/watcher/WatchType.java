package com.uddernetworks.mspaint.watcher;

import org.eclipse.lsp4j.FileChangeType;

import java.nio.file.WatchEvent;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Maps to FileChangedType from the LSP specification
 * https://microsoft.github.io/language-server-protocol/specification#workspace_didChangeWatchedFiles
 */
public enum WatchType {
    CREATE(1),
    MODIFY(2),
    DELETE(3);

    private int id;

    WatchType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public FileChangeType toFCT() {
        return FileChangeType.forValue(this.id);
    }

    public static WatchType fromKind(WatchEvent.Kind<?> kind) {
        if (kind.equals(ENTRY_CREATE)) {
            return CREATE;
        } else if (kind.equals(ENTRY_MODIFY)) {
            return MODIFY;
        } else if (kind.equals(ENTRY_DELETE)) {
            return DELETE;
        } else {
            return MODIFY;
        }
    }
}
