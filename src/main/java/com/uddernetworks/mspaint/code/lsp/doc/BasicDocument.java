package com.uddernetworks.mspaint.code.lsp.doc;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.lsp.LanguageServerWrapper;
import com.uddernetworks.mspaint.code.lsp.RequestManager;
import com.uddernetworks.mspaint.util.IDEFileUtils;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

public class BasicDocument implements Document {

    private static Logger LOGGER = LoggerFactory.getLogger(BasicDocument.class);

    private File file;
    private File hiddenClone;
    private boolean enableHiddenClone;
    private ImageClass imageClass;
    private String text;

    // I believe version is just used internally, and does not matter on its initial value
    private int version = 0;
    private boolean opened;

    private DidChangeTextDocumentParams changesParams;

    private LanguageServerWrapper lsWrapper;
    private RequestManager requestManager;
    private File relParent;

    public BasicDocument(ImageClass imageClass, LanguageServerWrapper lsWrapper) {
        this.file = imageClass.getInputImage();
        this.imageClass = imageClass;
        this.lsWrapper = lsWrapper;
        this.requestManager = lsWrapper.getRequestManager();
        this.hiddenClone = new File(file.getAbsolutePath().replaceAll("\\.png$", ""));

        this.text = imageClass.getText();

        this.changesParams = new DidChangeTextDocumentParams(new VersionedTextDocumentIdentifier(),
                Collections.singletonList(new TextDocumentContentChangeEvent()));

        changesParams.getTextDocument().setUri(getURI());
    }

    @Override
    public void open() {
        open(true);
    }

    public void open(boolean updateLSP) {
        this.opened = true;

        if (this.text == null) {
            this.imageClass.scan();
            if ((this.text = this.imageClass.getText()) == null) return;
        }

        if (!updateLSP) return;
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

        rewriteFile();
    }

    private void rewriteFile() {
        if (enableHiddenClone && hiddenClone != null) {
            try {
                if (hiddenClone.createNewFile()) IDEFileUtils.setHidden(hiddenClone);
                Files.write(hiddenClone.toPath(), text.getBytes());
            } catch (IOException e) {
                LOGGER.error("Error updating hidden clone!", e);
            }
        }
    }

    @Override
    public void notifyOfTextChange() {
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
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean isOpened() {
        return opened;
    }

    @Override
    public void enableHiddenClone(boolean enabled) {
        var pref = enableHiddenClone;
        if (pref != (enableHiddenClone = enabled) && text != null) rewriteFile();
    }

    @Override
    public boolean isHiddenCloneEnabled() {
        return enableHiddenClone;
    }

    @Override
    public void setHiddenClone(File file) {
        hiddenClone = file;
    }

    @Override
    public File getHiddenClone() {
        return hiddenClone;
    }

    @Override
    public void setUseRelativeToDirectory(File relParent) {
        this.relParent = relParent;
        changesParams.getTextDocument().setUri(getURI());
    }

    /**
     * Returns the URI to be used when sending file data to the LSP server, which removes the .png file extension.
     *
     * @return The .png-removed URI
     */
    private String getURI() {
        var nonRelPath = this.file.toURI().toString();
        if (this.relParent != null) nonRelPath = this.relParent.toPath().relativize(this.file.toPath()).toString();
        return nonRelPath.replaceAll("\\.png?$", "");
    }
}
