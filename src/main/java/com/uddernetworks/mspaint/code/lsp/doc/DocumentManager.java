package com.uddernetworks.mspaint.code.lsp.doc;

import com.uddernetworks.mspaint.code.ImageClass;

import java.util.List;

public interface DocumentManager {
    void openFile(ImageClass imageClass);

    void openDocument(Document document);

    void closeFile(ImageClass imageClass);

    void closeDocument(Document document);

    Document getDocument(ImageClass imageClass);

    List<Document> getAllOpenedDocuments();
}
