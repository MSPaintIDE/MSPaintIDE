package com.uddernetworks.mspaint.code.languages.python;

import com.uddernetworks.mspaint.code.languages.DefaultJFlexLexer;
import com.uddernetworks.mspaint.code.languages.LanguageHighlighter;

public class PythonLanguageHighlighter implements LanguageHighlighter {

    @Override
    public DefaultJFlexLexer getHighlighter() {
//        return null;
        return new PythonLexer();
    }
}
