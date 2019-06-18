package com.uddernetworks.mspaint.code.lsp;

import com.uddernetworks.mspaint.code.lsp.doc.DocumentManager;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.Future;

import static com.uddernetworks.mspaint.code.lsp.LSStatus.INITIALIZED;
import static com.uddernetworks.mspaint.code.lsp.LSStatus.STARTED;

public class LanguageServerWrapper {

    private static Logger LOGGER = LoggerFactory.getLogger(LanguageServerWrapper.class);

    private LSStatus status = LSStatus.STOPPED;

    private RequestManager requestManager;
    private LanguageServer languageServer;
    private Future<Void> launcherFuture;
    private LSPClient client;

    private DocumentManager documentManager;

    private static final String TEMP_ROOT = System.getenv("lsp_demo"); // Example: C:\\MSPaintIDEDemos\\project

    public static void main(String[] args) throws InterruptedException {
        new LanguageServerWrapper().main();
    }

    private void main() {
        //        CompletableFuture.runAsync(() -> {
        client = new LSPClient();

        try {
//                ExecutorService executorService = Executors.newCachedThreadPool();

//                Socket socket = new Socket("localhost", 1044);
//                socket.setKeepAlive(true);
//                InputStream inputStream = socket.getInputStream();
//                OutputStream outputStream = socket.getOutputStream();

            var streamConnectionProvider = new BetterProvider(Arrays.asList(
                    "java",
                    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044",
                    "-Declipse.application=org.eclipse.jdt.ls.core.id1",
                    "-Dosgi.bundles.defaultStartLevel=4",
                    "-Declipse.product=org.eclipse.jdt.ls.core.product",
                    "-Dlog.level=ALL",
                    "-noverify",
                    "-Xmx1G",
                    "-jar",
                    "E:/MSPaintIDE/jdt-language-server-latest/plugins/org.eclipse.equinox.launcher_1.5.400.v20190515-0925.jar",
                    "-configuration",
                    "E:/MSPaintIDE/jdt-language-server-latest/config_win",
                    "-data",
                    "\"" + TEMP_ROOT + "\"",
                    "--add-modules=ALL-SYSTEM",
                    "--add-opens",
                    "java.base/java.util=ALL-UNNAMED",
                    "--add-opens",
                    "java.base/java.lang=ALL-UNNAMED"
            ), TEMP_ROOT);

            streamConnectionProvider.start();

//                System.setOut(new PrintStream(streamConnectionProvider.getOutputStream()));

//                Launcher<LanguageServer> launcher =
//                        Launcher.createLauncher(client, LanguageServer.class, streamConnectionProvider.getInputStream(), streamConnectionProvider.getOutputStream(), executorService, consumer -> message -> {
//                            LOGGER.debug("Got: " + message.getJsonrpc());
//                        });

            Launcher<LanguageServer> launcher =
                    Launcher.createLauncher(client, LanguageServer.class, streamConnectionProvider.getInputStream(), streamConnectionProvider.getOutputStream());

            languageServer = launcher.getRemoteProxy();
            client.connect(languageServer);
            launcherFuture = launcher.startListening();

            documentManager = new DocumentManager(this);

            languageServer.initialize(getInitParams()).thenApply(res -> {
                LOGGER.info("Started {}", res);

                requestManager = new DefaultRequestManager(this, languageServer, client, res.getCapabilities());
                setStatus(STARTED);
                requestManager.initialized(new InitializedParams());
                setStatus(INITIALIZED);

//                languageServer.getWorkspaceService().didChangeWorkspaceFolders(
//                        new DidChangeWorkspaceFoldersParams(
//                                new WorkspaceFoldersChangeEvent(
//                                        Arrays.asList(
//                                                new WorkspaceFolder(new File(TEMP_ROOT).toURI().toString())
//                                        ), Collections.emptyList())));
//                languageServer.getWorkspaceService().didChangeWorkspaceFolders();

                var workspaceService = languageServer.getWorkspaceService();
//                workspaceService.didChangeConfiguration();

//                WorkspaceFoldersChangeEvent event = new WorkspaceFoldersChangeEvent();
//                event.getAdded().add(getWorkspace(TEMP_ROOT, "Temp"));
//                DidChangeWorkspaceFoldersParams params = new DidChangeWorkspaceFoldersParams();
//                params.setEvent(event);
//                workspaceService.didChangeWorkspaceFolders(params);

                // Temp

                return res;
            }).thenRun(() -> {
                documentManager.openFile(new File(TEMP_ROOT + "\\src\\Main.java"));
            });
//                var params = new InitializeParams();
//                params.setWorkspaceFolders(List.of(new WorkspaceFolder(new File(TEMP_ROOT).toURI().toString())));
//                var stuff = proxy.initialize(params);
            LOGGER.info("End of proxy!");

        } catch (Exception e) {
            LOGGER.error("Can't launch language server for project", e);
        }

        LOGGER.info("End!");

//            try {
//                Thread.sleep(100_000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

//        });
    }

    private WorkspaceFolder getWorkspace(String file, String name) {
        var workspace = new WorkspaceFolder();
        workspace.setUri(new File(file).toURI().toString());
        workspace.setName(name);
        return workspace;
    }

    private String getURI(String file) {
        return URI.create(file).toString();
    }

    // Init params from this method modified from LSP4IntelliJ ( Copyright (c) 2018-2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved )
    private InitializeParams getInitParams() {
        InitializeParams initParams = new InitializeParams();
//        initParams.setClientName("MS Paint IDE");
//        initParams.setWorkspaceFolders(List.of(getWorkspace(TEMP_ROOT, "Temp")));
//        initParams.setWorkspaceFolders(Arrays.asList(new WorkspaceFolder(new File(TEMP_ROOT).toURI().toString())));
        initParams.setRootUri(new File(TEMP_ROOT).toURI().toString());
        WorkspaceClientCapabilities workspaceClientCapabilities = new WorkspaceClientCapabilities();
//        workspaceClientCapabilities.setApplyEdit(true);
//        workspaceClientCapabilities.setDidChangeWatchedFiles(new DidChangeWatchedFilesCapabilities());
//        workspaceClientCapabilities.setExecuteCommand(new ExecuteCommandCapabilities());
//        workspaceClientCapabilities.setWorkspaceEdit(new WorkspaceEditCapabilities());
//        workspaceClientCapabilities.setSymbol(new SymbolCapabilities());
        workspaceClientCapabilities.setWorkspaceFolders(true);
//        workspaceClientCapabilities.setConfiguration(false);

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

    public LSStatus getStatus() {
        return status;
    }

    public void setStatus(LSStatus status) {
        this.status = status;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public LanguageServer getLanguageServer() {
        return languageServer;
    }

    public LSPClient getClient() {
        return client;
    }
}
