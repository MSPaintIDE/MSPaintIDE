package com.uddernetworks.mspaint.code.lsp.doc;

import com.uddernetworks.mspaint.code.ImageClass;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * The DocumentManager is in charge of storing and creating {@link Document}s, along with
 */
public interface DocumentManager {

    /**
     * Opens the given {@link ImageClass}. This should occur before a text modification if not already opened, or when
     * the document is actually opened via Paint.
     *
     * @param imageClass The {@link ImageClass} to open
     */
    void openFile(ImageClass imageClass);

    /**
     * Opens the given {@link Document}. This should occur before a text modification if not already opened, or when the
     * document is actually opened via Paint.
     *
     * @param document The {@link Document} to open
     */
    void openDocument(Document document);

    /**
     * Closes the opened {@link Document} from the given {@link ImageClass}. If the {@link ImageClass} has no
     * {@link Document} associated with it, it will just return.
     *
     * @param imageClass The {@link ImageClass} to close
     */
    void closeFile(ImageClass imageClass);

    /**
     * Closes the opened {@link Document} given.
     *
     * @param document The {@link Document} to close
     */
    void closeDocument(Document document);

    /**
     * Tells the LSP server that the given {@link Document} has been deleted, if it exists. This also removes the
     * {@link Document} from the internal list of {@link Document}s.
     *
     * @param document The {@link Document} to delete
     */
    void deleteDocument(Document document);

    /**
     * Gets the existing {@link Document} for the given file. If one is not found, it is created and returned.
     *
     * @param file The file to get the {@link Document} of
     * @return The {@link Document} for the file
     */
    Document getDocument(File file);

    /**
     * Gets the existing {@link Document} for the given file. If one is not found and createdIfNotFound is true, it is
     * created and returned. If the parameter is false, the empty Optional is returned.
     *
     * @param file The file to check
     * @param createIfNotFound If a {@link Document} should be created
     * @return The {@link Document} for the given file
     */
    Optional<Document> getDocument(File file, boolean createIfNotFound);

    /**
     * Gets the {@link Document} for the given {@link ImageClass}. If one does not exist already, it is created and then
     * returned.
     *
     * @param imageClass The {@link ImageClass} to get the {@link Document} of
     * @return The {@link Document} for the {@link ImageClass}
     */
    Document getDocument(ImageClass imageClass);

    /**
     * Gets all the created {@link Document}s for the current {@link DocumentManager}.
     *
     * @return All {@link Document}s created
     */
    List<Document> getAllDocuments();

    /**
     * Gets all {@link Document}s opened by {@link DocumentManager#openDocument(Document)} or
     * {@link DocumentManager#openFile(ImageClass)}.
     *
     * @return All {@link Document}s opened
     */
    List<Document> getAllOpenedDocuments();
}
