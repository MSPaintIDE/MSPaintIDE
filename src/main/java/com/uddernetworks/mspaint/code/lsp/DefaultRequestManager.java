package com.uddernetworks.mspaint.code.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.CancelParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DefaultRequestManager implements RequestManager {

    private static Logger LOGGER = LoggerFactory.getLogger(DefaultRequestManager.class);

    private LanguageServerWrapper wrapper;
    private LanguageServer server;
    private LanguageClient client;
    private ServerCapabilities serverCapabilities;
    private TextDocumentSyncOptions textDocumentOptions;
    private WorkspaceService workspaceService;
    private TextDocumentService textDocumentService;

    public DefaultRequestManager(LanguageServerWrapper wrapper, LanguageServer server, LanguageClient client,
                                 ServerCapabilities serverCapabilities) {

        this.wrapper = wrapper;
        this.server = server;
        this.client = client;
        this.serverCapabilities = serverCapabilities;

        textDocumentOptions = serverCapabilities.getTextDocumentSync().isRight() ?
                serverCapabilities.getTextDocumentSync().getRight() :
                null;
        workspaceService = server.getWorkspaceService();
        textDocumentService = server.getTextDocumentService();
    }

    public LanguageServerWrapper getWrapper() {
        return wrapper;
    }

    public LanguageClient getClient() {
        return client;
    }

    public LanguageServer getServer() {
        return server;
    }

    public ServerCapabilities getServerCapabilities() {
        return serverCapabilities;
    }

    //Client
    @Override
    public void showMessage(MessageParams messageParams) {
        client.showMessage(messageParams);
    }

    @Override
    public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams showMessageRequestParams) {
        return client.showMessageRequest(showMessageRequestParams);
    }

    @Override
    public void logMessage(MessageParams messageParams) {
        client.logMessage(messageParams);
    }

    @Override
    public void telemetryEvent(Object o) {
        client.telemetryEvent(o);
    }

    @Override
    public CompletableFuture<Void> registerCapability(RegistrationParams params) {
        return client.registerCapability(params);
    }

    @Override
    public CompletableFuture<Void> unregisterCapability(UnregistrationParams params) {
        return client.unregisterCapability(params);
    }

    @Override
    public CompletableFuture<ApplyWorkspaceEditResponse> applyEdit(ApplyWorkspaceEditParams params) {
        return client.applyEdit(params);
    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams publishDiagnosticsParams) {
        client.publishDiagnostics(publishDiagnosticsParams);
    }

    @Override
    public void semanticHighlighting(SemanticHighlightingParams params) {
        client.semanticHighlighting(params);
    }

    //Server
    //General
    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        if (checkStatus()) {
            try {
                return server.initialize(params);
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void initialized(InitializedParams params) {
        if (wrapper.getStatus() == LSStatus.STARTED) {
            try {
                server.initialized(params);
            } catch (Exception e) {
                crashed(e);
            }
        }
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        if (checkStatus()) {
            try {
                return server.shutdown();
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }

    }

    @Override
    public void exit() {
        if (checkStatus()) {
            try {
                server.exit();
            } catch (Exception e) {
                crashed(e);
            }
        }
    }

    @Override
    public void cancelRequest(CancelParams params) {

    }

    //Workspace
    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
        if (checkStatus()) {
            try {
                workspaceService.didChangeConfiguration(params);
            } catch (Exception e) {
                crashed(e);
            }
        }
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        if (checkStatus()) {
            try {
                workspaceService.didChangeWatchedFiles(params);
            } catch (Exception e) {
                crashed(e);
            }
        }
    }

    public CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams params) {
        if (checkStatus()) {
            try {
                return serverCapabilities.getWorkspaceSymbolProvider() ? workspaceService.symbol(params) : null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else
            return null;
    }

    public CompletableFuture<Object> executeCommand(ExecuteCommandParams params) {
        if (checkStatus()) {
            try {
                return serverCapabilities.getExecuteCommandProvider() != null ?
                        workspaceService.executeCommand(params) :
                        null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    //Document
    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        if (checkStatus()) {
            try {
                if (textDocumentOptions == null || textDocumentOptions.getOpenClose()) {
                    textDocumentService.didOpen(params);
                }
            } catch (Exception e) {
                crashed(e);
            }
        }
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        if (checkStatus()) {
            try {
                if (textDocumentOptions == null || textDocumentOptions.getChange() != null) {
                    textDocumentService.didChange(params);
                }
            } catch (Exception e) {
                crashed(e);
            }
        }
    }

    @Override
    public void willSave(WillSaveTextDocumentParams params) {
        if (checkStatus()) {
            try {
                if (textDocumentOptions == null || textDocumentOptions.getWillSave()) {
                    textDocumentService.willSave(params);
                }
            } catch (Exception e) {
                crashed(e);
            }
        }
    }

    @Override
    public CompletableFuture<List<TextEdit>> willSaveWaitUntil(WillSaveTextDocumentParams params) {
        if (checkStatus()) {
            try {
                return (textDocumentOptions == null || textDocumentOptions.getWillSaveWaitUntil()) ?
                        textDocumentService.willSaveWaitUntil(params) :
                        null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        if (checkStatus()) {
            try {
                if (textDocumentOptions == null || textDocumentOptions.getSave() != null) {
                    textDocumentService.didSave(params);
                }
            } catch (Exception e) {
                crashed(e);
            }
        }
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        if (checkStatus()) {
            try {
                if (textDocumentOptions == null || textDocumentOptions.getOpenClose()) {
                    textDocumentService.didClose(params);
                }
            } catch (Exception e) {
                crashed(e);
            }
        }
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {
        if (checkStatus()) {
            try {
                return (serverCapabilities.getCompletionProvider() != null) ?
                        textDocumentService.completion(params) :
                        null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CompletableFuture<CompletionItem> completionItemResolve(CompletionItem unresolved) {
        if (checkStatus()) {
            try {
                return (serverCapabilities.getCompletionProvider() != null && serverCapabilities.getCompletionProvider()
                        .getResolveProvider()) ? textDocumentService.resolveCompletionItem(unresolved) : null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CompletableFuture<Hover> hover(TextDocumentPositionParams params) {
        if (checkStatus()) {
            try {
                return (serverCapabilities.getHoverProvider()) ? textDocumentService.hover(params) : null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CompletableFuture<SignatureHelp> signatureHelp(TextDocumentPositionParams params) {
        if (checkStatus())
            try {
                return (serverCapabilities.getSignatureHelpProvider() != null) ?
                        textDocumentService.signatureHelp(params) :
                        null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        else {
            return null;
        }
    }

    @Override
    public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
        if (checkStatus()) {
            try {
                return (serverCapabilities.getReferencesProvider()) ? textDocumentService.references(params) : null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(TextDocumentPositionParams params) {
        if (checkStatus()) {
            try {
                return (serverCapabilities.getDocumentHighlightProvider()) ?
                        textDocumentService.documentHighlight(params) :
                        null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(
            DocumentSymbolParams params) {
        if (checkStatus()) {
            try {
                return (serverCapabilities.getDocumentSymbolProvider()) ?
                        textDocumentService.documentSymbol(params) :
                        null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {
        if (checkStatus()) {
            try {
                return (serverCapabilities.getDocumentFormattingProvider()) ?
                        textDocumentService.formatting(params) :
                        null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> rangeFormatting(DocumentRangeFormattingParams params) {
        if (checkStatus()) {
            try {
                return (serverCapabilities.getDocumentRangeFormattingProvider() != null) ?
                        textDocumentService.rangeFormatting(params) :
                        null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> onTypeFormatting(DocumentOnTypeFormattingParams params) {
        if (checkStatus()) {
            try {
                return (serverCapabilities.getDocumentOnTypeFormattingProvider() != null) ?
                        textDocumentService.onTypeFormatting(params) :
                        null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(TextDocumentPositionParams params) {
        if (checkStatus()) {
            try {
                return (serverCapabilities.getDefinitionProvider()) ? textDocumentService.definition(params) : null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(CodeActionParams params) {
        if (checkStatus()) {
            try {
                return checkCodeActionProvider(serverCapabilities.getCodeActionProvider()) ?
                        textDocumentService.codeAction(params) :
                        null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {
        if (checkStatus()) {
            try {
                return (serverCapabilities.getCodeLensProvider() != null) ? textDocumentService.codeLens(params) : null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CompletableFuture<CodeLens> resolveCodeLens(CodeLens unresolved) {
        if (checkStatus()) {
            try {
                return (serverCapabilities.getCodeLensProvider() != null && serverCapabilities.getCodeLensProvider()
                        .isResolveProvider()) ? textDocumentService.resolveCodeLens(unresolved) : null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CompletableFuture<List<DocumentLink>> documentLink(DocumentLinkParams params) {
        if (checkStatus()) {
            try {
                return (serverCapabilities.getDocumentLinkProvider() != null) ?
                        textDocumentService.documentLink(params) :
                        null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public CompletableFuture<DocumentLink> documentLinkResolve(DocumentLink unresolved) {
        if (checkStatus()) {
            try {
                return (serverCapabilities.getDocumentLinkProvider() != null && serverCapabilities
                        .getDocumentLinkProvider().getResolveProvider()) ?
                        textDocumentService.documentLinkResolve(unresolved) :
                        null;
            } catch (Exception e) {
                crashed(e);
                return null;
            }
        } else {
            return null;
        }
    }

    public CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
        //        if (checkStatus()) {
        //            try {
        //                return (checkProvider((Either<Boolean, StaticRegistrationOptions>)serverCapabilities.getRenameProvider())) ?
        //                        textDocumentService.rename(params) :
        //                        null;
        //            } catch (Exception e) {
        //                crashed(e);
        //                return null;
        //            }
        //        } else {
        //            return null;
        //        }
        return null;
    }

    @Override
    public CompletableFuture<List<? extends Location>> implementation(TextDocumentPositionParams params) {
        return null;
    }

    @Override
    public CompletableFuture<List<? extends Location>> typeDefinition(TextDocumentPositionParams params) {
        return null;
    }

    @Override
    public CompletableFuture<List<ColorInformation>> documentColor(DocumentColorParams params) {
        return null;
    }

    @Override
    public CompletableFuture<List<ColorPresentation>> colorPresentation(ColorPresentationParams params) {
        return null;
    }

    @Override
    public CompletableFuture<List<FoldingRange>> foldingRange(FoldingRangeRequestParams params) {
        return null;
    }

    public boolean checkStatus() {
        return wrapper.getStatus() == LSStatus.INITIALIZED;
    }

    private void crashed(Exception e) {
        LOGGER.warn("Crashed!", e);
//        wrapper.crashed(e);
    }

    private boolean checkProvider(Either<Boolean, StaticRegistrationOptions> provider) {
        return provider != null && ((provider.isLeft() && provider.getLeft()) || (provider.isRight()
                && provider.getRight() != null));
    }

    private boolean checkCodeActionProvider(Either<Boolean, CodeActionOptions> provider) {
        return provider != null && ((provider.isLeft() && provider.getLeft()) || (provider.isRight()
                && provider.getRight() != null));
    }
}
