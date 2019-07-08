package com.uddernetworks.mspaint.code.lsp;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.lsp.doc.Document;
import com.uddernetworks.mspaint.code.lsp.doc.DocumentManager;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * A 'wrapper' of sorts, for the language servers. It is <b>highly</b> recommended to have one instance of the wrapper
 * per language server.
 */
public interface LanguageServerWrapper {

    /**
     * Starts the LSP server with the given root path.
     *
     * @param rootPath The root path of the project; may be null if in headless mode
     * @return The {@link CompletableFuture}
     */
    CompletableFuture<Void> start(File rootPath);

    /**
     * Opens the workspace, should be ran even if the LSP does not support workspaces. Only one workspace is used,
     * as the IDE only allows one instance open at once.
     *
     * @param file The workspace path
     * @param inputFile The source directory
     */
    void openWorkspace(File file, File inputFile);

    /**
     * Writes to the same directory of the document with the same name, excluding `.png`. This only works if
     * {@link LanguageServerWrapper#writeOnChange()} has been invoked.
     *
     * @param document The {@link Document} to write
     */
    void writeIfApplicable(Document document);

    /**
     * Highlights the given {@link Document}.
     *
     * @param document The {@link Document} to highlight
     */
    void highlightFile(Document document);

    /**
     * Closes the workspace by the given directory.
     *
     * @param file The directory of the workspace
     */
    void closeWorkspace(File file);

    /**
     * Opens the given {@link ImageClass}, independent of a workspace. This is currently not used, but probably should\
     * be in the future.
     *
     * @param imageClass The {@link ImageClass} to open
     */
    void openFile(ImageClass imageClass);

    /**
     * Verifies that the LSP server for the root path is fully running. If this is not ran and things are attempted to
     * be sent to the LSP server, NPEs may occur, and/or unknown problems (Would be different for every LSP server) may
     * occur.
     *
     * @param rootPath The root path of the workspace
     * @return The {@link CompletableFuture}
     */
    CompletableFuture<Void> verifyStatus(File rootPath);

    /**
     * Sets the supplier to give the LSP server executable's directory.
     *
     * @param supplier The supplier to give the LSP server executable's directory
     * @return The current {@link LanguageServerWrapper}
     */
    LanguageServerWrapper setServerDirectorySupplier(Supplier<String> supplier);

    /**
     * Gets the {@link InitializeParams} for the wrapper.
     *
     * @return The {@link InitializeParams} for the wrapper
     */
    InitializeParams getInitParams();

    /**
     * Sets the argument preprocessor, to change any data from the LSP command arguments. This may not be used in
     * implementations other than the default one, and is not mandatory for all servers.
     *
     * @param argumentPreprocessor The function giving the current {@link LanguageServerWrapper} and a list of the
     *                             arguments to invoke the LSP server, returning the modified arguments
     * @return The current {@link LanguageServerWrapper}
     */
    LanguageServerWrapper argumentPreprocessor(BiFunction<LanguageServerWrapper, List<String>, List<String>> argumentPreprocessor);

    /**
     * Gets the root path of the workspace.
     *
     * @return The root path of the workspace
     */
    Optional<File> getRootPath();

    /**
     * Gets the {@link LSStatus} of the LSP server.
     *
     * @return The {@link LSStatus} of the LSP server
     */
    LSStatus getStatus();

    /**
     * Sets the {@link LSStatus} of the server. Should only be ran if it has truly reached the status, as nothing other
     * than the setting of an internal variable should be set in this method.
     *
     * @param status The {@link LSStatus} to set
     */
    void setStatus(LSStatus status);

    /**
     * Gets the {@link RequestManager} used.
     *
     * @return The {@link RequestManager} used
     */
    RequestManager getRequestManager();

    /**
     * Gets the {@link DocumentManager} used.
     *
     * @return The {@link DocumentManager} used
     */
    DocumentManager getDocumentManager();

    /**
     * Gets the {@link LanguageServer} used.
     *
     * @return The {@link LanguageServer} used
     */
    LanguageServer getLanguageServer();

    /**
     * Gets the {@link LSPClient} created and used.
     *
     * @return The {@link LSPClient} created and used
     */
    LSPClient getClient();

    /**
     * Gets the {@link LSP} the wrapper is for.
     *
     * @return The {@link LSP} the wrapper is for
     */
    LSP getLSP();

    /**
     * Gets the path of the LSP server, invoking the Supplier from
     * {@link LanguageServerWrapper#setServerDirectorySupplier(Supplier)}.
     *
     * @return The path of the LSP server
     */
    String getServerPath();

    /**
     * Sets the wrapper to use the input as the workspace, required in some LSP servers.
     *
     * @return The current {@link LanguageServerWrapper}
     */
    LanguageServerWrapper useInputAsWorkspace();

    /**
     * Sets the wrapper to write files on change, invoking {@link LanguageServerWrapper#writeIfApplicable(Document)}.
     * This is required in some LSP servers.
     *
     * @return The current {@link LanguageServerWrapper}
     */
    LanguageServerWrapper writeOnChange();
}
