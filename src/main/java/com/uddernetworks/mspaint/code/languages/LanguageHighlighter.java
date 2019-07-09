package com.uddernetworks.mspaint.code.languages;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.lexer.javascript.JavaScriptLexer;
import com.uddernetworks.mspaint.code.lexer.javascript.JavaScriptParser;
import com.uddernetworks.mspaint.texteditor.LetterGenerator;
import org.antlr.v4.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import static com.uddernetworks.mspaint.gui.window.search.ReplaceManager.grabRealSub;
import static com.uddernetworks.mspaint.gui.window.search.ReplaceManager.trimImage;

public class LanguageHighlighter {

    private static Logger LOGGER = LoggerFactory.getLogger(LanguageHighlighter.class);

    public static void main(String[] args) {
        var input = CharStreams.fromString("");
        var lex = new JavaScriptLexer(input);
        // copy text out of sliding buffer and store in tokens
        lex.setTokenFactory(new CommonTokenFactory(true));
        var tokens = new UnbufferedTokenStream<CommonToken>(lex);
        var parser = new JavaScriptParser(tokens);
        parser.setBuildParseTree(false);
        var voc = parser.getVocabulary();

        System.out.println("\n");
        for (int i = 0; i < voc.getMaxTokenType(); i++) {
            System.out.print("\"" + voc.getDisplayName(i) + "\", ");
        }
    }

    public void highlight(Language language, ImageClass imageClass) {
        try {
            var highlightData = language.getHighlightData();
            String text = imageClass.getText();
            if (text == null) return; // Silently fail

            var input = CharStreams.fromString(text);
            var lex = highlightData.getLexer(input);
            lex.setTokenFactory(new CommonTokenFactory(true));
            var tokens = new UnbufferedTokenStream<CommonToken>(lex);
            var parser = highlightData.getParser(tokens);
            parser.setBuildParseTree(false);
            var vocabulary = parser.getVocabulary();

            var original = imageClass.getScannedImage().orElseThrow().getOriginalImage();

            var i2 = 0;
            for (Token token; (token = tokens.get(i2)).getType() != Token.EOF; i2++, tokens.consume()) {
                if (token.getText().isBlank()) continue;
                var from = token.getCharPositionInLine();
                var to = from + token.getText().length();
                var colorOptional = highlightData.getColor(vocabulary.getDisplayName(token.getType()));
                var color = colorOptional.orElse(Color.MAGENTA);

                var line = imageClass.getScannedImage().orElseThrow().getLine(token.getLine() - 1);
                for (int i = from; i < Math.min(to, line.size()); i++) {
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
