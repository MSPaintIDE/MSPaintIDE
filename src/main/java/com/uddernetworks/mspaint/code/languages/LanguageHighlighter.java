package com.uddernetworks.mspaint.code.languages;

import com.uddernetworks.newocr.ScannedImage;
import com.uddernetworks.newocr.character.ImageLetter;

import javax.swing.text.Segment;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public interface LanguageHighlighter {
    DefaultJFlexLexer getHighlighter();

    default void highlight(ScannedImage scannedImage) {
        String text = scannedImage.getPrettyString();

        DefaultJFlexLexer highlighter = getHighlighter();

        List<Token> toks = new ArrayList<>(text.length() / 10);
        long ts = System.nanoTime();
        int len = text.length();
        try {
            Segment seg = new Segment();
            seg.array = text.toCharArray();
            seg.offset = 0;
            seg.count = text.length();
            highlighter.parse(seg, 0, toks);
        } finally {
            System.out.println(String.format("Parsed %d in %d ms, giving %d tokens\n",
                    len, (System.nanoTime() - ts) / 1000000, toks.size()));
        }

        toks.forEach(token -> {
            TokenType type = token.getTokenType();

            int start = token.start;
            for (int i = 0; i < token.length; i++) {
                char cha = text.charAt(start + i);
                if (cha == '\n') continue;
                ImageLetter imageLetter = scannedImage.letterAt(start + i);
                imageLetter.setData(new Color(type.getStyle()));
            }
        });
    }
}
