package com.uddernetworks.mspaint.code.lsp.doc;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.lsp.LanguageServerWrapper;
import com.uddernetworks.mspaint.code.lsp.RequestManager;
import org.eclipse.lsp4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;

public class BasicDocument implements Document {

    private static Logger LOGGER = LoggerFactory.getLogger(BasicDocument.class);

    private File file;
    private ImageClass imageClass;
    private String text;

    // I believe version is just used internally, and does not matter on its initial value
    private int version = 0;
    private boolean opened;

    private DidChangeTextDocumentParams changesParams;

    private LanguageServerWrapper lsWrapper;
    private RequestManager requestManager;

    public BasicDocument(ImageClass imageClass, LanguageServerWrapper lsWrapper) {
        this.file = imageClass.getInputImage();
        this.imageClass = imageClass;
        this.lsWrapper = lsWrapper;
        this.requestManager = lsWrapper.getRequestManager();

        this.text = imageClass.getText();

        this.changesParams = new DidChangeTextDocumentParams(new VersionedTextDocumentIdentifier(),
                Collections.singletonList(new TextDocumentContentChangeEvent()));

        changesParams.getTextDocument().setUri(getURI());
    }

    @Override
    public void open() {
        this.opened = true;

        LOGGER.info("requestManager = " + requestManager);
        LOGGER.info("text = " + text);
        LOGGER.info("getURI() = " + getURI());
        if (this.text == null) {
            this.imageClass.scan();
            if ((this.text = this.imageClass.getText()) == null) return;
        }

        this.requestManager.didOpen(new DidOpenTextDocumentParams(new TextDocumentItem(getURI(), "java", version++, this.text)));
    }

    @Override
    public void close() {
        this.opened = false;

        this.requestManager.didClose(new DidCloseTextDocumentParams(new TextDocumentIdentifier(getURI())));
    }

    @Override
    public void delete() {
        this.opened = false;

        // Closing via the LSP is already handled via the LanguageServerWrapper, above the switch
    }

    @Override
    public void modifyText(String text) {
        this.text = text;

        changesParams.getTextDocument().setVersion(version++);

        // Sync settings *should* be incremental, but will be full since I'm lazy
        changesParams.getContentChanges().get(0).setText(getText());
        requestManager.didChange(changesParams);
    }

    @Override
    public void notifyOfTextChange() {
        LOGGER.info("Notifying of text change");
        this.imageClass.scan();
        modifyText(this.imageClass.getText());
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public ImageClass getImageClass() {
        return imageClass;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean isOpened() {
        return opened;
    }

    /**
     * Returns the URI to be used when sending file data to the LSP server, which removes the .png file extension.
     *
     * @return The .png-removed URI
     */
    private String getURI() {
        return this.file.toURI().toString().replaceAll("\\.png?$", "");
    }
}
