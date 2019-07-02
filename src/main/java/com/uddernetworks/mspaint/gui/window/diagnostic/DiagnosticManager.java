package com.uddernetworks.mspaint.gui.window.diagnostic;

import org.eclipse.lsp4j.Diagnostic;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface DiagnosticManager {
    void setDiagnostics(List<Diagnostic> diagnostics, String uri);

    List<Map.Entry<String, Diagnostic>> getDiagnostics();

    void onDiagnosticChange(Consumer<List<Map.Entry<String, Diagnostic>>> onChange);

    void openGUI();

    void pauseDiagnostics();

    void resumeDiagnostics();
}
