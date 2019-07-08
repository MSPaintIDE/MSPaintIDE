package com.uddernetworks.mspaint.code.lsp;

import com.uddernetworks.mspaint.main.StartupLogic;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LSPClient implements LanguageClient {

    private static Logger LOGGER = LoggerFactory.getLogger(LSPClient.class);

    private StartupLogic startupLogic;
    private LanguageServer server;


    public LSPClient(StartupLogic startupLogic) {
        this.startupLogic = startupLogic;
    }

    public final void connect(LanguageServer server) {
        this.server = server;
    }

    protected final LanguageServer getLanguageServer() {
        return server;
    }

    @JsonNotification("language/status")
    public void languageStatus(Map<Object, Object> data) {
        LOGGER.info("[{}] {}", data.get("type"), data.get("message"));
    }

    @JsonNotification("java/didChangeWorkspaceFolders")
    public void didChangeWorkspaceFolders(Object object) {
        LOGGER.info("[didChangeWorkspaceFolders] {}", object);
    }

    @Override
    public void telemetryEvent(Object object) {
        LOGGER.info("[telemetryEvent] {}", object);
    }

    @Override
    public final CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
        LOGGER.info("[showMessageRequest] {}", requestParams);
        return CompletableFuture.supplyAsync(MessageActionItem::new);
    }

    @Override
    public final void showMessage(MessageParams messageParams) {
        LOGGER.info("[showMessage] {}", messageParams);
    }

    @Override
    public final void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        if (diagnostics.getDiagnostics().isEmpty()) LOGGER.info("Diagnostics empty for {}", diagnostics.getUri());
        diagnostics.getDiagnostics().forEach(diagnostic -> {
            LOGGER.warn("[Diagnostic] [{}] {}", diagnostic.getSeverity(), diagnostic.getMessage());
        });

        this.startupLogic.getDiagnosticManager().setDiagnostics(diagnostics.getDiagnostics(), diagnostics.getUri());
    }

    @Override
    public final void logMessage(MessageParams message) {
        LOGGER.info(message.getMessage());
    }

    @Override
    public final CompletableFuture<ApplyWorkspaceEditResponse> applyEdit(ApplyWorkspaceEditParams params) {
        LOGGER.info("[applyEdit] {}", params);
        return CompletableFuture.supplyAsync(() -> new ApplyWorkspaceEditResponse(true));
    }

    @Override
    public CompletableFuture<Void> registerCapability(RegistrationParams params) {
        LOGGER.info("[registerCapability] {}", params);
        return CompletableFuture.runAsync(() -> {});
    }

    @Override
    public CompletableFuture<Void> unregisterCapability(UnregistrationParams params) {
        LOGGER.info("[unregisterCapability] {}", params);
        return CompletableFuture.runAsync(() -> {});
    }

    @Override
    public CompletableFuture<List<WorkspaceFolder>> workspaceFolders() {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @Override
    public  CompletableFuture<List<Object>> configuration(ConfigurationParams configurationParams) {
        LOGGER.info("[configuration] {}", configurationParams);
        return CompletableFuture.supplyAsync(() -> Arrays.asList(new Object()));
    }
}