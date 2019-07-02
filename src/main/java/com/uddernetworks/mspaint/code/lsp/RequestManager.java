/*
 * Copyright (c) 2018-2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uddernetworks.mspaint.code.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.CancelParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RequestManager {

    //Client
    void showMessage(MessageParams messageParams);

    CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams showMessageRequestParams);

    void logMessage(MessageParams messageParams);

    void telemetryEvent(Object o);

    CompletableFuture<Void> registerCapability(RegistrationParams params);

    CompletableFuture<Void> unregisterCapability(UnregistrationParams params);

    CompletableFuture<ApplyWorkspaceEditResponse> applyEdit(ApplyWorkspaceEditParams params);

    void publishDiagnostics(PublishDiagnosticsParams publishDiagnosticsParams);

    //Server
    //General
    CompletableFuture<InitializeResult> initialize(InitializeParams params);

    void initialized(InitializedParams params);

    CompletableFuture<Object> shutdown();

    void exit();

    void cancelRequest(CancelParams params);

    //Workspace
    void didChangeConfiguration(DidChangeConfigurationParams params);

    void didChangeWatchedFiles(DidChangeWatchedFilesParams params);

    CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams params);

    CompletableFuture<Object> executeCommand(ExecuteCommandParams params);

    //Document
    void didOpen(DidOpenTextDocumentParams params);

    void didChange(DidChangeTextDocumentParams params);

    void willSave(WillSaveTextDocumentParams params);

    CompletableFuture<List<TextEdit>> willSaveWaitUntil(WillSaveTextDocumentParams params);

    void didSave(DidSaveTextDocumentParams params);

    void didClose(DidCloseTextDocumentParams params);

    CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params);

    CompletableFuture<CompletionItem> completionItemResolve(CompletionItem unresolved);

    CompletableFuture<Hover> hover(TextDocumentPositionParams params);

    CompletableFuture<SignatureHelp> signatureHelp(TextDocumentPositionParams params);

    CompletableFuture<List<? extends Location>> references(ReferenceParams params);

    CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(TextDocumentPositionParams params);

    CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(DocumentSymbolParams params);

    CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params);

    CompletableFuture<List<? extends TextEdit>> rangeFormatting(DocumentRangeFormattingParams params);

    CompletableFuture<List<? extends TextEdit>> onTypeFormatting(DocumentOnTypeFormattingParams params);

    CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(TextDocumentPositionParams params);

    CompletableFuture<List<Either<Command, CodeAction>>> codeAction(CodeActionParams params);

    CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params);

    CompletableFuture<CodeLens> resolveCodeLens(CodeLens unresolved);

    CompletableFuture<List<DocumentLink>> documentLink(DocumentLinkParams params);

    CompletableFuture<DocumentLink> documentLinkResolve(DocumentLink unresolved);

    CompletableFuture<WorkspaceEdit> rename(RenameParams params);

    CompletableFuture<List<? extends Location>> implementation(TextDocumentPositionParams params);

    CompletableFuture<List<? extends Location>> typeDefinition(TextDocumentPositionParams params);

    CompletableFuture<List<ColorInformation>> documentColor(DocumentColorParams params);

    CompletableFuture<List<ColorPresentation>> colorPresentation(ColorPresentationParams params);

    CompletableFuture<List<FoldingRange>> foldingRange(FoldingRangeRequestParams params);

    void semanticHighlighting(SemanticHighlightingParams params);
}
