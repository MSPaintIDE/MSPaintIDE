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

public class DefaultDiagnosticManager implements DiagnosticManager {

    private boolean paused = false;
    private StartupLogic startupLogic;
    private DiagnosticWindow diagnosticWindow;
    private List<Map.Entry<String, Diagnostic>> diagnostics = new ArrayList<>();
    private List<Consumer<List<Map.Entry<String, Diagnostic>>>> onChange = new ArrayList<>();

    public DefaultDiagnosticManager(StartupLogic startupLogic) {
        this.startupLogic = startupLogic;
    }

    @Override
    public void setDiagnostics(List<Diagnostic> diagnostics, String uri) {
        this.diagnostics.removeIf(entry -> entry.getKey().equals(uri));
        diagnostics.stream().map(diagnostic -> new AbstractMap.SimpleEntry<>(uri, diagnostic)).forEach(this.diagnostics::add);
        if (!this.paused) this.onChange.forEach(change -> change.accept(this.diagnostics));
    }

    @Override
    public List<Map.Entry<String, Diagnostic>> getDiagnostics() {
        return diagnostics;
    }

    @Override
    public void onDiagnosticChange(Consumer<List<Map.Entry<String, Diagnostic>>> onChange) {
        this.onChange.add(onChange);
        if (!this.paused && !this.diagnostics.isEmpty()) onChange.accept(this.diagnostics);
    }

    @Override
    public void openGUI() {
        if (MainGUI.HEADLESS) return;
        if (this.diagnosticWindow != null && this.diagnosticWindow.isShowing()) return;

        try {
            (this.diagnosticWindow = new DiagnosticWindow(this.startupLogic.getMainGUI(), this)).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pauseDiagnostics() {
        this.paused = true;
    }

    @Override
    public void resumeDiagnostics() {
        this.paused = false;
        this.onChange.forEach(change -> change.accept(this.diagnostics));
    }
}
