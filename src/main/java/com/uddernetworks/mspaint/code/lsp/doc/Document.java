package com.uddernetworks.mspaint.code.lsp.doc;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.lsp.LanguageServerWrapper;
import com.uddernetworks.mspaint.code.lsp.RequestManager;
import org.eclipse.lsp4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

public class Document {

    private static Logger LOGGER = LoggerFactory.getLogger(Document.class);

    private File file;
    private ImageClass imageClass;
    private String text;

    // I believe version is just used internally, and does not matter on its initial value
    private int version = 0;
    private boolean opened;

    private DidChangeTextDocumentParams changesParams;

    private LanguageServerWrapper lsWrapper;
    private RequestManager requestManager;

    public Document(ImageClass imageClass, LanguageServerWrapper lsWrapper) {
        this.file = imageClass.getInputImage();
        this.imageClass = imageClass;
        this.lsWrapper = lsWrapper;
        this.requestManager = lsWrapper.getRequestManager();

        try {
            this.text = Files.readString(file.toPath());
        } catch (IOException e) {
            LOGGER.error("Error while creating Document", e);
        }

        this.changesParams = new DidChangeTextDocumentParams(new VersionedTextDocumentIdentifier(),
                Collections.singletonList(new TextDocumentContentChangeEvent()));

        changesParams.getTextDocument().setUri(getURI());
    }

    public void open() {
        this.opened = true;

        System.out.println("requestManager = " + requestManager);
        System.out.println("text = " + text);
        System.out.println("getURI() = " + getURI());
        requestManager.didOpen(new DidOpenTextDocumentParams(new TextDocumentItem(getURI(), "java", version++, this.text)));
    }

    public void close() {
        this.opened = false;

        requestManager.didClose(new DidCloseTextDocumentParams(new TextDocumentIdentifier(getURI())));
    }

    public void modifyText(String text) {
        this.text = text;

        changesParams.getTextDocument().setVersion(version++);

        // Sync settings *should* be incremental, but will be full since I'm lazy
        changesParams.getContentChanges().get(0).setText(getText());
        requestManager.didChange(changesParams);
    }

    public File getFile() {
        return file;
    }

    public String getURI() {
        return this.file.toURI().toString();
    }

    public String getText() {
        return text;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }
}
