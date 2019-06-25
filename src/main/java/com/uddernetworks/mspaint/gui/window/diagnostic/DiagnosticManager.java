package com.uddernetworks.mspaint.gui.window.diagnostic;

import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.StartupLogic;
import org.eclipse.lsp4j.Diagnostic;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DiagnosticManager {

    private StartupLogic startupLogic;
    private DiagnosticWindow diagnosticWindow;
    private List<Map.Entry<String, Diagnostic>> diagnostics = new ArrayList<>();
    private Consumer<List<Map.Entry<String, Diagnostic>>> onChange;

    public DiagnosticManager(StartupLogic startupLogic) {
        this.startupLogic = startupLogic;
    }

    public void setDiagnostics(List<Diagnostic> diagnostics, String uri) {
        this.diagnostics.removeIf(entry -> entry.getKey().equals(uri));
        diagnostics.stream().map(diagnostic -> new AbstractMap.SimpleEntry<>(uri, diagnostic)).forEach(this.diagnostics::add);
        if (this.onChange != null) this.onChange.accept(this.diagnostics);
    }

    public List<Map.Entry<String, Diagnostic>> getDiagnostics() {
        return diagnostics;
    }

    public void onDiagnosticChange(Consumer<List<Map.Entry<String, Diagnostic>>> onChange) {
        (this.onChange = onChange).accept(this.diagnostics);
    }

    public void openGUI() {
        if (MainGUI.HEADLESS) return;
        if (this.diagnosticWindow != null && this.diagnosticWindow.isShowing()) return;

        try {
            (this.diagnosticWindow = new DiagnosticWindow(this.startupLogic.getMainGUI(), this)).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
