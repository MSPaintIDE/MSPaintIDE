package com.uddernetworks.mspaint.code.languages;

import com.uddernetworks.newocr.recognition.ScannedImage;

public class LanguageHighlighter {

    // TODO: Rework
    public void highlight(ScannedImage scannedImage) {
        /*
        String text = scannedImage.getPrettyString();

        List<Token> toks = new ArrayList<>(text.length() / 10);

        Segment seg = new Segment();
        seg.array = text.toCharArray();
        seg.count = text.length();
        lexer.parse(seg, 0, toks);

        toks.forEach(token -> {
            TokenType type = token.getTokenType();

            int start = token.start;
            for (int i = 0; i < token.length; i++) {
                char cha = text.charAt(start + i);
                if (cha == '\n') continue;
                scannedImage.letterAt(start + i).ifPresent(imageLetter -> imageLetter.setData(new Color(type.getStyle())));
            }
        });
        */
    }

}
