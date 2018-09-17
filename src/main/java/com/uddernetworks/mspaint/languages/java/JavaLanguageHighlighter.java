package com.uddernetworks.mspaint.languages.java;

import com.uddernetworks.mspaint.languages.LanguageHighlighter;
import com.uwyn.jhighlight.highlighter.ExplicitStateHighlighter;
import com.uwyn.jhighlight.highlighter.JavaHighlighter;

public class JavaLanguageHighlighter implements LanguageHighlighter {

    @Override
    public String getCssClass(int style) {
        switch (style) {
            case 1:
                return "000,000,000,"; // plain
            case 2:
                return "000,000,000,"; // keyword
            case 3:
                return "000,044,221,"; // type
            case 4:
                return "000,124,031,"; // operator
            case 5:
                return "000,033,255,"; // separator
            case 6:
                return "188,000,000,"; // literal
            case 7:
                return "147,147,147,"; // comment
            case 8:
                return "147,147,147,"; // javadoc comment
            case 9:
                return "147,147,147,"; // javadoc tag
            default:
                return null;
        }
    }

    @Override
    public ExplicitStateHighlighter getHighlighter() {
        JavaHighlighter highlighter = new JavaHighlighter();
        JavaHighlighter.ASSERT_IS_KEYWORD = true;
        return highlighter;
    }
}
