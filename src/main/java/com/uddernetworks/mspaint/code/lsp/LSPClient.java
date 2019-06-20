package com.uddernetworks.mspaint.code.lsp;

import com.uddernetworks.mspaint.gui.window.search.ReplaceManager;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LSPClient implements LanguageClient {

    private static Logger LOGGER = LoggerFactory.getLogger(ReplaceManager.class);

//    private LSPDiagnosticsToMarkers diagnosticHandler;

    private LanguageServer server;
//    private LanguageServerWrapper wrapper;

    public final void connect(LanguageServer server) {
        this.server = server;
//        this.wrapper = wrapper;
//        this.diagnosticHandler = new LSPDiagnosticsToMarkers(wrapper.serverDefinition.id);
    }

    protected final LanguageServer getLanguageServer() {
        return server;
    }

    @JsonNotification("language/status")
    public void languageStatus(Map<Object, Object> data) {
        LOGGER.info("[{}] {}", data.get("type"), data.get("message"));
    }

    @Override
    public void telemetryEvent(Object object) {
        // TODO
        LOGGER.info("shidd telemetryEvent {}", object);
    }

    @Override
    public final CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
        LOGGER.info("shidd showMessageRequest {}", requestParams);
//        return ServerMessageHandler.showMessageRequest(requestParams);
        return CompletableFuture.supplyAsync(MessageActionItem::new);
    }

    @Override
    public final void showMessage(MessageParams messageParams) {
        LOGGER.info("shidd showMessage {}", messageParams);
//        ServerMessageHandler.showMessage(wrapper.serverDefinition.label, messageParams);
    }

    @Override
    public final void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
//        this.diagnosticHandler.accept(diagnostics);
        LOGGER.info("shidd publishDiagnostics {}", diagnostics);
    }

    @Override
    public final void logMessage(MessageParams message) {
//        CompletableFuture.runAsync(() -> ServerMessageHandler.logMessage(wrapper, message));
        LOGGER.info(message.getMessage());
    }

    @Override
    public final CompletableFuture<ApplyWorkspaceEditResponse> applyEdit(ApplyWorkspaceEditParams params) {
        LOGGER.info("shidd applyEdit {}", params);
        return CompletableFuture.supplyAsync(() -> {
            return new ApplyWorkspaceEditResponse(true);
        });
//        return CompletableFuture.supplyAsync(() -> {
//            Job job = new Job(Messages.serverEdit) {
//                @Override
//                public IStatus run(IProgressMonitor monitor) {
//                    LSPEclipseUtils.applyWorkspaceEdit(params.getEdit());
//                    return Status.OK_STATUS;
//                }
//            };
//            job.schedule();
//            try {
//                job.join();
//                return new ApplyWorkspaceEditResponse(true);
//            } catch (InterruptedException e) {
//                LanguageServerPlugin.logError(e);
//                Thread.currentThread().interrupt();
//                return new ApplyWorkspaceEditResponse(Boolean.FALSE);
//            }
//        });
    }

    @Override
    public CompletableFuture<Void> registerCapability(RegistrationParams params) {
//        return CompletableFuture.runAsync(() -> wrapper.registerCapability(params));
        LOGGER.info("shidd registerCapability {}", params);
        return CompletableFuture.runAsync(() -> {});
    }

    @Override
    public CompletableFuture<Void> unregisterCapability(UnregistrationParams params) {
//        return CompletableFuture.runAsync(() -> wrapper.unregisterCapability(params));
        LOGGER.info("shidd unregisterCapability {}", params);
        return CompletableFuture.runAsync(() -> {});
    }

    @Override
    public CompletableFuture<List<WorkspaceFolder>> workspaceFolders() {
//        List<WorkspaceFolder> res = new ArrayList<>(wrapper.allWatchedProjects.size());
//        for (final IProject project : wrapper.allWatchedProjects) {
//            res.add(LSPEclipseUtils.toWorkspaceFolder(project));
//        }
        LOGGER.info("shidd workspaceFolders");
        return CompletableFuture.completedFuture(Collections.emptyList());
    }
}