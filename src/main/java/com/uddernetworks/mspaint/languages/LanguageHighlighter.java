package com.uddernetworks.mspaint.languages;

import java.io.IOException;

public interface LanguageHighlighter {
    String highlight(String text) throws IOException;
}
