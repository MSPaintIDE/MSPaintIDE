package com.uddernetworks.mspaint.code.lsp.doc;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.lsp.LanguageServerWrapper;
import com.uddernetworks.mspaint.main.StartupLogic;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultDocumentManager implements DocumentManager {

    private Map<File, Document> documentMap = new HashMap<>();
    private LanguageServerWrapper lsWrapper;
    private StartupLogic startupLogic;

    public DefaultDocumentManager(LanguageServerWrapper lsWrapper, StartupLogic startupLogic) {
        this.lsWrapper = lsWrapper;
        this.startupLogic = startupLogic;
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
        getDocument(imageClass.getInputImage(), false).ifPresent(this::closeDocument);
    }

    @Override
    public void closeDocument(Document document) {
        document.close();
    }

    @Override
    public void deleteDocument(Document document) {
        closeDocument(document);
        document.delete();
        this.documentMap.remove(document.getFile());
    }

    @Override
    public Document getDocument(File file) {
        return this.documentMap.computeIfAbsent(file, x -> new BasicDocument(new ImageClass(file, this.startupLogic.getMainGUI()), lsWrapper));
    }

    @Override
    public Optional<Document> getDocument(File file, boolean createIfNotFound) {
        return Optional.ofNullable(this.documentMap.computeIfAbsent(file, x -> {
            if (!createIfNotFound) return null;
            return new BasicDocument(new ImageClass(file, this.startupLogic.getMainGUI()), lsWrapper);
        }));
    }

    @Override
    public Document getDocument(ImageClass imageClass) {
        return this.documentMap.computeIfAbsent(imageClass.getInputImage(), file -> new BasicDocument(imageClass, lsWrapper));
    }

    @Override
    public List<Document> getAllDocuments() {
        return new ArrayList<>(documentMap.values());
    }

    @Override
    public List<Document> getAllOpenedDocuments() {
        return documentMap.values().stream().filter(Document::isOpened).collect(Collectors.toList());
    }

}
