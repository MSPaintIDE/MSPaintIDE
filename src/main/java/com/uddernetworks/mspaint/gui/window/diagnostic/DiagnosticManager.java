package com.uddernetworks.mspaint.gui.window.diagnostic;

import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.StartupLogic;
import org.eclipse.lsp4j.Diagnostic;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class DiagnosticManager {

    private StartupLogic startupLogic;
    private DiagnosticWindow diagnosticWindow;
    private List<Diagnostic> diagnostics;
    private Consumer<List<Diagnostic>> onChange;

    public DiagnosticManager(StartupLogic startupLogic) {
        this.startupLogic = startupLogic;
    }

    public void setDiagnostics(List<Diagnostic> diagnostics) {
        this.diagnostics = diagnostics;
        if (this.onChange != null) this.onChange.accept(diagnostics);
    }

    public List<Diagnostic> getDiagnostics() {
        return diagnostics;
    }

    public void onDiagnosticChange(Consumer<List<Diagnostic>> onChange) {
        this.onChange = onChange;
        if (this.diagnostics != null) onChange.accept(this.diagnostics);
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
