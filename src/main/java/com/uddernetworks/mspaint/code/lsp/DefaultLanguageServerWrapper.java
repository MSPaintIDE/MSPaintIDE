package com.uddernetworks.mspaint.code.lsp;

import com.google.common.base.CharMatcher;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.lsp.doc.DefaultDocumentManager;
import com.uddernetworks.mspaint.code.lsp.doc.Document;
import com.uddernetworks.mspaint.code.lsp.doc.DocumentManager;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.StartupLogic;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.watcher.FileWatchManager;
import com.uddernetworks.mspaint.watcher.FileWatcher;
import com.uddernetworks.mspaint.watcher.WatchType;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.uddernetworks.mspaint.code.lsp.LSStatus.*;

public class DefaultLanguageServerWrapper implements LanguageServerWrapper {

    private static Logger LOGGER = LoggerFactory.getLogger(DefaultLanguageServerWrapper.class);

    private LSStatus status = LSStatus.STOPPED;
    private File rootPath;

    private RequestManager requestManager;
    private LanguageServer languageServer;
    private Future<Void> launcherFuture;
    private LSPClient client;

    private DocumentManager documentManager;
    private BiConsumer<LanguageServerWrapper, File> workspaceInit;
    private CompletableFuture<Void> startingFuture;

    private ObservableList<WorkspaceFolder> workspaces = FXCollections.observableArrayList();

    private StartupLogic startupLogic;
    private FileWatchManager fileWatchManager;
    private LSP lsp;
    private Supplier<String> serverPath;
    private List<String> lspArgs;
    private BiFunction<LanguageServerWrapper, List<String>, List<String>> argumentPreprocessor = (x, y) -> y;
    private boolean useInputForWorkspace;
    private File inputFile;
    private boolean writeOnChange;

    public DefaultLanguageServerWrapper(StartupLogic startupLogic, LSP lsp, List<String> lspArgs) {
        this(startupLogic, lsp, null, lspArgs);
    }

    public DefaultLanguageServerWrapper(StartupLogic startupLogic, LSP lsp, String serverPath, List<String> lspArgs) {
        this(startupLogic, lsp, serverPath, lspArgs, null);
    }

    public DefaultLanguageServerWrapper(StartupLogic startupLogic, LSP lsp, String serverPath, List<String> lspArgs, BiConsumer<LanguageServerWrapper, File> workspaceInit) {
        this.documentManager = new DefaultDocumentManager(this, startupLogic);
        this.startupLogic = startupLogic;
        this.fileWatchManager = startupLogic.getFileWatchManager();
        this.lsp = lsp;
        this.serverPath = () -> serverPath;
        this.lspArgs = lspArgs;
        this.workspaceInit = workspaceInit;

        if (!lsp.usesWorkspaces()) return;
        this.workspaces.addListener((ListChangeListener<WorkspaceFolder>) change -> {
            var added = new ArrayList<WorkspaceFolder>();
            var removed = new ArrayList<WorkspaceFolder>();

            while (change.next()) {
                added.addAll(change.getAddedSubList());
                removed.addAll(change.getRemoved());
            }

            LOGGER.info("Adding: {}  Removing: {}", added, removed);

            this.languageServer.getWorkspaceService().didChangeWorkspaceFolders(
                    new DidChangeWorkspaceFoldersParams(
                            new WorkspaceFoldersChangeEvent(
                                    added,
                                    removed)));
        });
    }

    @Override
    public CompletableFuture<Void> start(File rootPath) {
        setStatus(STARTING);
        this.client = new LSPClient(this.startupLogic);

        this.rootPath = rootPath;

        try {
            var processedArgs = this.argumentPreprocessor.apply(this, new ArrayList<>(this.lspArgs));

            var streamConnectionProvider = new LSPProvider(
                    () -> requestManager,
                    processedArgs,
                    serverPath.get()); // new File(TEMP_ROOT).getParent()
            streamConnectionProvider.start();

            Launcher<LanguageServer> launcher =
                    Launcher.createLauncher(client, LanguageServer.class, streamConnectionProvider.getInputStream(), streamConnectionProvider.getOutputStream());

            languageServer = launcher.getRemoteProxy();
            client.connect(languageServer);
            launcherFuture = launcher.startListening();

            return (startingFuture = languageServer.initialize(getInitParams()).thenApply(res -> {
                LOGGER.info("Started LSP");

                requestManager = new DefaultRequestManager(this, languageServer, client, res.getCapabilities());
                setStatus(STARTED);
                requestManager.initialized(new InitializedParams());
                setStatus(INITIALIZED);
                return res;
            }).thenRun(() -> LOGGER.info("Done starting LSP!")));

        } catch (Exception e) {
            LOGGER.error("Can't launch language server for project", e);
        }

        return CompletableFuture.runAsync(() -> {});
    }

    @Override
    public void openWorkspace(File file, File inputFile) {
        this.inputFile = inputFile;
        // Should this throw if `file` is not in `rootPath`?
        // ^ Current solution is no, as it's pretty dependant on the server and language weather or not it will cause
        // problems.
        verifyStatus(file.getParentFile()).thenRun(() -> {

            if (this.workspaceInit != null) {
                LOGGER.info("Running language-specific workspace init code...");
                this.workspaceInit.accept(this, file);
            }

            LOGGER.info("Adding workspace {}", file.getAbsolutePath());
            this.workspaces.add(getWorkspace(file));

            // This is NOT done in the Document class, because stuff may get messed up when deleting and mainly creating
            // new files.
            LOGGER.info("Watching {}", inputFile.getAbsolutePath());

            var watcher = this.fileWatchManager.watchFile(inputFile);
            var lang = this.startupLogic.getCurrentLanguage();
            var highlight = lang.getHighlightOption();
            var highlightDir = new File[] {null};
            lang.getLanguageSettings().<File>onChangeSetting(highlight, changed -> highlightDir[0] = changed, true);

            var dotMatcher = CharMatcher.is('.');
            watcher.addFileFiler(filtering -> filtering.isFile() && filtering.getName().endsWith(".png") && dotMatcher.countIn(filtering.getName()) >= 2);
            watcher.addFileFiler(filtering -> !isInSubDirectory(highlightDir[0], filtering));

            // Opening all paths because the Java LSP server listens to files itself
            var diagnosticManager = this.startupLogic.getDiagnosticManager();
            diagnosticManager.pauseDiagnostics();
            try {
                Files.walk(inputFile.toPath(), FileVisitOption.FOLLOW_LINKS)
                        .map(Path::toFile)
                        .filter(File::isFile)
                        .filter(walking -> walking.getName().endsWith(".png"))
                        .forEach(path -> {
                            try {
                                if (!watcher.keepFromFilters(path)) return;
                                LOGGER.info("Walked to file {} vs {}\tfile is {}", path.toPath(), path.getAbsoluteFile().toPath(), file.toPath());
                                var document = this.documentManager.getDocument(path);
                                if (this.useInputForWorkspace) document.setUseRelativeToDirectory(file);
                                writeIfApplicable(document);
                                document.open();
                                highlightFile(document);
                            } catch (Exception e) {
                                LOGGER.error("Error", e);
                            }
                        });
            } catch (IOException e) {
                LOGGER.error("An error has occurred while initially walking workspace files", e);
            }

            diagnosticManager.resumeDiagnostics();

            watcher.addListener((type, changedFile) -> {
                changedFile = changedFile.getAbsoluteFile();
                LOGGER.info("Changed: {}", changedFile.getAbsolutePath());

                if (lsp.usesWorkspaces()) {
                    // Not sure if this should happen before or after the following switch? It has seemed to work fine with
                    // Java, Python, Go and JS so far, so I'm leaving this the same.
                    this.languageServer.getWorkspaceService().didChangeWatchedFiles(new DidChangeWatchedFilesParams(Arrays.asList(
                            new FileEvent(changedFile.toURI().toString(), type.toFCT())
                    )));
                }

                Document document = null;
                switch (type) {
                    case CREATE:
                        LOGGER.info("Create document event {}", changedFile.getAbsolutePath());
                        writeIfApplicable(document = this.documentManager.getDocument(changedFile));
                        if (this.useInputForWorkspace) document.setUseRelativeToDirectory(file);
                        document.open();
                        break;
                    case MODIFY:
                        LOGGER.info("Modify document event {}", changedFile.getAbsolutePath());
                        document = this.documentManager.getDocument(changedFile);

                        var imageClass = document.getImageClass();
                        imageClass.scan();
                        document.setText(imageClass.getText());

                        writeIfApplicable(document);

                        if (!document.isOpened()) document.open();

                        document.modifyText(document.getText());
                        break;
                    case DELETE:
                        LOGGER.info("Delete document event {}", changedFile.getAbsolutePath());
                        this.documentManager.getDocument(changedFile, false).ifPresent(this.documentManager::deleteDocument);
                        break;
                }

                if (document != null) {
                    var imageClass = document.getImageClass();
                    this.startupLogic.runRPC(rpcManager -> rpcManager.setFileEditing(imageClass.getInputImage().getName()));
                    if (type == WatchType.CREATE && imageClass.getScannedImage().isPresent()) highlightFile(document);
                }
            });
        });
    }

    @Override
    public void writeIfApplicable(Document document) {
        if (this.writeOnChange) {
            var writingFile = new File(document.getFile().getAbsolutePath().replaceAll("\\.png$", ""));
            LOGGER.info("Writing to {} from image path being {}", writingFile.getAbsolutePath(), document.getFile().getAbsolutePath());
            try {
                var imageClass = document.getImageClass();
                if (imageClass.getScannedImage().isEmpty()) {
                    imageClass.scan();
                    document.setText(imageClass.getText());
                }

                if (writingFile.createNewFile()) setHidden(writingFile);
                Files.write(writingFile.toPath(), document.getText().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                LOGGER.error("Error while writing to file after creation/modification", e);
            }
        }
    }

    @Override
    public void highlightFile(Document document) {
        try {
            this.startupLogic.getCurrentLanguage().highlightAll(Collections.singletonList(document.getImageClass()));
        } catch (IOException e) {
            LOGGER.error("There was an error trying to highlight the created/modified file " + document.getFile().getName(), e);
        }
    }

    @Override
    public void closeWorkspace(File file) {
        verifyStatus(file.getParentFile()).thenRun(() -> {
            LOGGER.info("Closing workspace {}", file.getAbsolutePath());
            this.workspaces.removeIf(workspace -> workspace.getUri().equals(file.toURI().toString()));
            this.fileWatchManager.getWatcher(file).ifPresent(FileWatcher::stopWatching);
        });
    }

    @Override
    public void openFile(ImageClass imageClass) {
        verifyStatus(null).thenRun(() -> {
            LOGGER.info("Opening file {}", imageClass.getInputImage().getAbsolutePath());
            this.documentManager.openFile(imageClass);
        });
    }

    @Override
    public CompletableFuture<Void> verifyStatus(File rootPath) {
        if (getStatus() == STARTED || getStatus() == STARTING) {
            LOGGER.info("LSP server is starting up, waiting for it to finish...");
            return this.startingFuture;
        } else if (getStatus() == STOPPED) {
            LOGGER.info("LSP is stopped, waiting for it to start up...");
            return start(rootPath);
        } // INITIALIZED
        return CompletableFuture.runAsync(() -> {});
    }

    private WorkspaceFolder getWorkspace(File file) {
        return getWorkspace(file.getAbsolutePath());
    }

    private WorkspaceFolder getWorkspace(String file) {
        var workspace = new WorkspaceFolder();
        workspace.setUri(getURI(file));
        workspace.setName(ProjectManager.getPPFProject().getName());
        return workspace;
    }

    private WorkspaceFolder getWorkspace(String file, String name) {
        var workspace = new WorkspaceFolder();
        workspace.setUri(getURI(file));
        workspace.setName(name);
        return workspace;
    }

    private static boolean isInSubDirectory(File dir, File file) {
        if (file == null) return false;
        if (file.equals(dir)) return true;
        return isInSubDirectory(dir, file.getParentFile());
    }

    @Override
    public LanguageServerWrapper setServerDirectorySupplier(Supplier<String> supplier) {
        this.serverPath = supplier;
        return this;
    }

    private void setHidden(File file) {
        try {
            Files.setAttribute(file.toPath(), "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS); //< set hidden attribute
        } catch (IOException e) {}
    }

    private String getURI(String file) {
        return new File(file).toURI().toString();
    }

    /*
     * Init params from this method modified from LSP4IntelliJ ( Copyright (c) 2018-2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved )
     */
    @Override
    public InitializeParams getInitParams() {
        InitializeParams initParams = new InitializeParams();
        if (this.rootPath != null) {
            var root = rootPath.toURI().toString();
            if (this.useInputForWorkspace) {
                root = rootPath.toPath().relativize(inputFile.toPath()).toUri().toString();
            }

            initParams.setRootUri(root);
            LOGGER.info("Workspace root is {}", root);
        }
        WorkspaceClientCapabilities workspaceClientCapabilities = new WorkspaceClientCapabilities();
        workspaceClientCapabilities.setApplyEdit(true);
        workspaceClientCapabilities.setDidChangeWatchedFiles(new DidChangeWatchedFilesCapabilities());
        workspaceClientCapabilities.setExecuteCommand(new ExecuteCommandCapabilities());
        workspaceClientCapabilities.setWorkspaceEdit(new WorkspaceEditCapabilities());
//        workspaceClientCapabilities.setSymbol(new SymbolCapabilities());
        workspaceClientCapabilities.setWorkspaceFolders(true);
        workspaceClientCapabilities.setConfiguration(true);

        TextDocumentClientCapabilities textDocumentClientCapabilities = new TextDocumentClientCapabilities();
        textDocumentClientCapabilities.setCodeAction(new CodeActionCapabilities());
        textDocumentClientCapabilities.setCompletion(new CompletionCapabilities(new CompletionItemCapabilities(false)));
        textDocumentClientCapabilities.setDefinition(new DefinitionCapabilities());
//        textDocumentClientCapabilities.setDocumentHighlight(new DocumentHighlightCapabilities());
        textDocumentClientCapabilities.setFormatting(new FormattingCapabilities());
//        textDocumentClientCapabilities.setHover(new HoverCapabilities());
//        textDocumentClientCapabilities.setOnTypeFormatting(new OnTypeFormattingCapabilities());
//        textDocumentClientCapabilities.setRangeFormatting(new RangeFormattingCapabilities());
        textDocumentClientCapabilities.setReferences(new ReferencesCapabilities());
        textDocumentClientCapabilities.setRename(new RenameCapabilities());
        textDocumentClientCapabilities.setSemanticHighlightingCapabilities(new SemanticHighlightingCapabilities(false));
        textDocumentClientCapabilities.setSignatureHelp(new SignatureHelpCapabilities());
        textDocumentClientCapabilities.setSynchronization(new SynchronizationCapabilities(true, true, true));
        initParams.setCapabilities(
                new ClientCapabilities(workspaceClientCapabilities, textDocumentClientCapabilities, null));
//        initParams.setInitializationOptions(null);
//        initParams.setInitializationOptions(
//                serverDefinition.getInitializationOptions(URI.create(initParams.getRootUri())));

        return initParams;
    }

    @Override
    public LanguageServerWrapper argumentPreprocessor(BiFunction<LanguageServerWrapper, List<String>, List<String>> argumentPreprocessor) {
        this.argumentPreprocessor = argumentPreprocessor;
        return this;
    }

    @Override
    public Optional<File> getRootPath() {
        return Optional.ofNullable(rootPath);
    }

    @Override
    public LSStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(LSStatus status) {
        this.status = status;
    }

    @Override
    public RequestManager getRequestManager() {
        return requestManager;
    }

    @Override
    public DocumentManager getDocumentManager() {
        return documentManager;
    }

    @Override
    public LanguageServer getLanguageServer() {
        return languageServer;
    }

    @Override
    public LSPClient getClient() {
        return client;
    }

    @Override
    public LSP getLSP() {
        return lsp;
    }

    @Override
    public String getServerPath() {
        return serverPath.get();
    }

    @Override
    public LanguageServerWrapper useInputAsWorkspace() {
        this.useInputForWorkspace = true;
        return this;
    }

    @Override
    public LanguageServerWrapper writeOnChange() {
        this.writeOnChange = true;
        return this;
    }

    public static File getLSPDirectory() {
        var file = new File(StartupLogic.getJarParent().orElse(new File("")), "lsp");
        if (MainGUI.DEV_MODE) {
            var envLsp = System.getenv("STATIC_LSP_DIRECTORY");
            file = new File(envLsp == null ? "C:\\Program Files (x86)\\MS Paint IDE\\lsp" : envLsp);
            if (!file.exists() && !file.mkdirs()) {
                LOGGER.error("The IDE is in development mode and the hard-coded LSP directory could not be created. " +
                        "Either create \"{}\" manually, or set the directory required to the environment variable 'STATIC_LSP_DIRECTORY' " +
                        "(This can be done by changing the variable with the same name in the build.gradle)", file.getAbsolutePath());
                System.exit(0);
            }
        }

        return file;
    }
}
