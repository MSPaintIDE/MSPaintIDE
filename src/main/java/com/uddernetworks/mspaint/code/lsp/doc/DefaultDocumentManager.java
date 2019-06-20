package com.uddernetworks.mspaint.code.lsp.doc;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.lsp.LanguageServerWrapper;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultDocumentManager implements DocumentManager {

    private Map<File, Document> documentMap = new HashMap<>();
    private LanguageServerWrapper lsWrapper;

    public DefaultDocumentManager(LanguageServerWrapper lsWrapper) {
        this.lsWrapper = lsWrapper;
    }

    @Override
    public void openFile(ImageClass imageClass) {
        openDocument(getDocument(imageClass));
    }

    @Override
    public void openDocument(Document document) {
        if (document.isOpened()) return;
        document.open();
    }

    @Override
    public void closeFile(ImageClass imageClass) {
        closeDocument(getDocument(imageClass));
    }

    @Override
    public void closeDocument(Document document) {
        document.close();
    }

    @Override
    public Document getDocument(ImageClass imageClass) {
        return this.documentMap.computeIfAbsent(imageClass.getInputImage(), file -> new Document(imageClass, lsWrapper));
    }

    @Override
    public List<Document> getAllOpenedDocuments() {
        return documentMap.values().stream().filter(Document::isOpened).collect(Collectors.toList());
    }

}
