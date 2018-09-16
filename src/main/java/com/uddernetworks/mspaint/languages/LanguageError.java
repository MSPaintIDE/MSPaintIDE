package com.uddernetworks.mspaint.languages;

public interface LanguageError {
    int getLineNumber();
    int getColumnNumber();
    String getSource();
    String getMessage();
}
