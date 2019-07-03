package com.uddernetworks.mspaint.code.lsp.doc;

import com.uddernetworks.mspaint.code.ImageClass;

import java.io.File;

public interface Document {

    /**
     * Opens the {@link Document}.
     */
    void open();

    /**
     * Closes the {@link Document}.
     */
    void close();

    /**
     * Deletes the {@link Document}.
     */
    void delete();

    /**
     * Sets the new text of the {@link Document}.
     *
     * @param text The text to set
     */
    void modifyText(String text);

    /**
     * Does the same thing as {@link Document#modifyText(String)}, but it forces the document to scan the internal
     * {@link ImageClass} to receive the text contents, then send it to the LSP server.
     */
    void notifyOfTextChange();

    /**
     * Gets the real File of the {@link Document}. This is most often a .png or other image file.
     *
     * @return The file
     */
    File getFile();

    /**
     * Gets the {@link ImageClass} used by the {@link Document}.
     *
     * @return The {@link ImageClass}
     */
    ImageClass getImageClass();

    /**
     * Get the current text of the {@link Document}.
     *
     * @return The current text
     */
    String getText();

    /**
     * Sets the current text of the {@link Document}, without firing any other event.
     *
     * @param text The text to set
     */
    void setText(String text);

    /**
     * If the {@link Document} is opened currently.
     *
     * @return If the {@link Document} is opened
     */
    boolean isOpened();

    void setUseRelativeToDirectory(File relParent);
}
