package com.uddernetworks.mspaint.code.lsp.doc;

import com.uddernetworks.mspaint.code.lsp.LanguageServerWrapper;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DocumentManager {

    private Map<File, Document> documentMap = new HashMap<>();
    private LanguageServerWrapper lsWrapper;

    public DocumentManager(LanguageServerWrapper lsWrapper) {
        this.lsWrapper = lsWrapper;
    }

    public void openFile(File file) {
        openDocument(getDocument(file));
    }

    public void openDocument(Document document) {
        if (document.isOpened()) return;
        document.open();
    }

    public void closeFile(File file) {
        closeDocument(getDocument(file));
    }

    public void closeDocument(Document document) {
        document.close();
    }

    public Document getDocument(File file) {
        return documentMap.getOrDefault(file, new Document(file, lsWrapper));
    }

    public List<Document> getAllOpenedDocuments() {
        return documentMap.values().stream().filter(Document::isOpened).collect(Collectors.toList());
    }

}
