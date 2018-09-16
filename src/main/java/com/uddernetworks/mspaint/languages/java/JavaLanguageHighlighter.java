package com.uddernetworks.mspaint.languages.java;

import com.uddernetworks.mspaint.languages.LanguageHighlighter;
import com.uwyn.jhighlight.highlighter.ExplicitStateHighlighter;
import com.uwyn.jhighlight.highlighter.JavaHighlighter;
import com.uwyn.jhighlight.tools.StringUtils;

import java.io.*;

public class JavaLanguageHighlighter implements LanguageHighlighter {

    private String getCssClass(int style) {
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

    public String highlight(String text) throws IOException {
        ExplicitStateHighlighter highlighter = this.getHighlighter();

        InputStream is = new ByteArrayInputStream(text.getBytes());
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader r = new BufferedReader(isr);

        StringBuilder builder = new StringBuilder();

        String line;
        char[] token;
        int length;
        int style;
        String css_class;
        while ((line = r.readLine()) != null) {
            line = StringUtils.convertTabsToSpaces(line, 4);

            Reader lineReader = new StringReader(line);
            highlighter.setReader(lineReader);
            int index = 0;
            while (index < line.length()) {
                style = highlighter.getNextToken();
                length = highlighter.getTokenLength();
                token = line.substring(index, index + length).toCharArray();

                for (char ignored : token) {
                    css_class = getCssClass(style);

                    if (css_class != null) {
                        builder.append(css_class);
                    }
                }
                index += length;
            }

            builder.append("\n");
        }

        return builder.toString();
    }

    private ExplicitStateHighlighter getHighlighter() {
        JavaHighlighter highlighter = new JavaHighlighter();
        JavaHighlighter.ASSERT_IS_KEYWORD = true;
        return highlighter;
    }
}
