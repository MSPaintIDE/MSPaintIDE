package com.uddernetworks.mspaint.code.languages.brainfuck;

import com.uddernetworks.mspaint.code.languages.LanguageError;

public class BrainfuckError implements LanguageError {

    private int lineNumber;
    private int columnNumber;
    private String source;
    private String message;

    public BrainfuckError(int lineNumber, int columnNumber, String source, String message) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.source = source;
        this.message = message;
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }

    @Override
    public int getColumnNumber() {
        return this.columnNumber;
    }

    @Override
    public String getSource() {
        return this.source;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
