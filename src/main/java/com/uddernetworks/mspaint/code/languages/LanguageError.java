package com.uddernetworks.mspaint.code.languages;

public interface LanguageError {
    int getLineNumber();
    int getColumnNumber();
    String getSource();
    String getMessage();
}
