package com.uddernetworks.mspaint.code.languages;

import com.uwyn.jhighlight.highlighter.ExplicitStateHighlighter;
import com.uwyn.jhighlight.tools.StringUtils;

import java.io.*;

public interface LanguageHighlighter {
    String getCssClass(int style);
    ExplicitStateHighlighter getHighlighter();

    default String highlight(String text) throws IOException {
        ExplicitStateHighlighter highlighter = getHighlighter();

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
}
