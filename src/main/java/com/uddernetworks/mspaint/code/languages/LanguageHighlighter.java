package com.uddernetworks.mspaint.code.languages;

import com.uddernetworks.code.lexer.Java9Lexer;
import com.uddernetworks.code.lexer.Java9Parser;
import com.uddernetworks.mspaint.texteditor.LetterGenerator;
import com.uddernetworks.newocr.recognition.ScannedImage;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.uddernetworks.mspaint.gui.window.search.ReplaceManager.grabRealSub;
import static com.uddernetworks.mspaint.gui.window.search.ReplaceManager.trimImage;

public class LanguageHighlighter {

    private static Logger LOGGER = LoggerFactory.getLogger(LanguageHighlighter.class);

    public void highlight(Language language, ScannedImage scannedImage) {
        try {
            var highlightData = language.getHighlightData();
            String text = scannedImage.getPrettyString();

            var input = CharStreams.fromString(text);
            var lex = new Java9Lexer(input);
            // copy text out of sliding buffer and store in tokens
            lex.setTokenFactory(new CommonTokenFactory(true));
            var tokens = new UnbufferedTokenStream<CommonToken>(lex);
            var parser = new Java9Parser(tokens);
            parser.setBuildParseTree(false);

            var original = scannedImage.getOriginalImage();

            for (var token : lex.getAllTokens()) {
                var from = token.getCharPositionInLine();
                var to = from + token.getText().length();
                var color = highlightData.getColor(lex.getTokenNames()[token.getType()]);

                var line = scannedImage.getLine(token.getLine() - 1);
                for (int i = from; i < to; i++) {
                    var letter = line.get(i);

                    // Could the following be improved?
                    var image = original.getSubimage(letter.getX(), letter.getY(), letter.getWidth(), letter.getHeight());
                    image = grabRealSub(original, letter);
                    image = trimImage(image);
                    var values = LetterGenerator.createGrid(image);
                    LetterGenerator.toGrid(image, values, color);

                    var grid = LetterGenerator.trim(values);

                    letter.setData(grid);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while highlighting!", e);
        }
    }
}
