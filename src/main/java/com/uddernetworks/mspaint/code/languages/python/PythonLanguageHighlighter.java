package com.uddernetworks.mspaint.code.languages.python;

import com.uddernetworks.mspaint.code.languages.LanguageHighlighter;
import com.uwyn.jhighlight.highlighter.ExplicitStateHighlighter;

public class PythonLanguageHighlighter implements LanguageHighlighter {

    @Override
    public String getCssClass(int style) {
        switch (style) {
            case 1:
                return 0x000000 + ",";
            case 2:
                return 0x000000 + ",";
            case 3:
                return 0x000000 + ",";
            case 4:
                return 0x000000 + ",";
            case 5:
                return 0x000000 + ",";
            case 6:
                return 0x3333ee + ",";
            case 7:
                return 0x3333ee + ",";
            case 8:
                return 0x000000 + ",";
            case 9:
                return 0x000000 + ",";
            case 10:
                return 0x000000 + ",";
            case 11:
                return 0xcc6600 + ",";
            case 12:
                return 0xcc6600 + ",";
            case 13:
                return 0x999933 + ",";
            case 14:
                return 0xcc6600 + ",";
            case 15:
                return 0x000000 + ",";
            case 16:
                return 0x339933 + ",";
            case 17:
                return 0x339933 + ",";
            case 18:
                return 0x000000 + ",";
            case 19:
                return 0xCC0000 + ",";
            case 20:
                return 0xCC0000 + ",";
            default:
                return null;
        }
    }

    @Override
    public ExplicitStateHighlighter getHighlighter() {
        return new PythonHighlighter();
    }
}
