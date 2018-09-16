package com.uddernetworks.mspaint.languages.java;

import com.uddernetworks.mspaint.languages.LanguageError;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.Locale;

public class JavaError implements LanguageError {

    private Diagnostic<? extends JavaFileObject> javaDiagnostic;

    public JavaError(Diagnostic<? extends JavaFileObject> javaDiagnostic) {
        this.javaDiagnostic = javaDiagnostic;
    }

    @Override
    public int getLineNumber() {
        return Long.valueOf(this.javaDiagnostic.getLineNumber()).intValue();
    }

    @Override
    public int getColumnNumber() {
        return Long.valueOf(this.javaDiagnostic.getColumnNumber()).intValue();
    }

    @Override
    public String getSource() {
        return this.javaDiagnostic.getSource().getName();
    }

    @Override
    public String getMessage() {
        return this.javaDiagnostic.getMessage(Locale.ENGLISH);
    }
}
