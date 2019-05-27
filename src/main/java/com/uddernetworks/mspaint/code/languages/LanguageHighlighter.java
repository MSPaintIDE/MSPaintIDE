package com.uddernetworks.mspaint.code.languages;

import com.uddernetworks.newocr.recognition.ScannedImage;

import javax.swing.text.Segment;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LanguageHighlighter {

    public void highlight(DefaultJFlexLexer lexer, ScannedImage scannedImage) {
        String text = scannedImage.getPrettyString();

        List<Token> toks = new ArrayList<>(text.length() / 10);

        Segment seg = new Segment();
        seg.array = text.toCharArray();
        seg.count = text.length();
        lexer.parse(seg, 0, toks);

        System.out.println("toks = " + toks);

        toks.forEach(token -> {
            TokenType type = token.getTokenType();

            int start = token.start;
            for (int i = 0; i < token.length; i++) {
                char cha = text.charAt(start + i);
                if (cha == '\n') continue;
                scannedImage.letterAt(start + i).ifPresent(imageLetter -> imageLetter.setData(new Color(type.getStyle())));
            }
        });
    }

}
