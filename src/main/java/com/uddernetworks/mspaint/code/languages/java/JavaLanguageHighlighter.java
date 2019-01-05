package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.languages.LanguageHighlighter;
import com.uwyn.jhighlight.highlighter.ExplicitStateHighlighter;
import com.uwyn.jhighlight.highlighter.JavaHighlighter;

public class JavaLanguageHighlighter implements LanguageHighlighter {

    @Override
    public String getCssClass(int style) {
        switch (style) {
            case 1:
                return 0x000000 + ","; // plain
            case 2:
                return 0x000000 + ","; // keyword
            case 3:
                return 0x002cdd + ","; // type
            case 4:
                return 0x007c1f + ","; // operator
            case 5:
                return 0x0021ff + ","; // separator
            case 6:
                return 0xbc0000 + ","; // literal
            case 7:
                return 0x939393 + ","; // comment
            case 8:
                return 0x939393 + ","; // javadoc comment
            case 9:
                return 0x939393 + ","; // javadoc tag
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
