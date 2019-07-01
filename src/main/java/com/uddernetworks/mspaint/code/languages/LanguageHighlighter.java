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

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.uddernetworks.mspaint.gui.window.search.ReplaceManager.grabRealSub;
import static com.uddernetworks.mspaint.gui.window.search.ReplaceManager.trimImage;

public class LanguageHighlighter {

    private static Logger LOGGER = LoggerFactory.getLogger(LanguageHighlighter.class);

    // TODO: Modularize
    private Map<List<String>, Color> tokenMap = Map.of(
            Arrays.asList("'open'", "'module'", "'requires'", "'exports'", "'to'", "'opens'", "'uses'", "'provides'", "'with'", "'transitive'", "'abstract'", "'assert'", "'boolean'", "'break'", "'byte'", "'case'", "'catch'", "'char'", "'class'", "'const'", "'continue'", "'default'", "'do'", "'double'", "'else'", "'enum'", "'extends'", "'final'", "'finally'", "'float'", "'for'", "'if'", "'goto'", "'implements'", "'import'", "'instanceof'", "'int'", "'interface'", "'long'", "'native'", "'new'", "'package'", "'private'", "'protected'", "'public'", "'return'", "'short'", "'static'", "'strictfp'", "'super'", "'switch'", "'synchronized'", "'this'", "'throw'", "'throws'", "'transient'", "'try'", "'void'", "'volatile'", "'while'", "BooleanLiteral", "CharacterLiteral", "'null'"), hex2Rgb("CC7832"), // Orange
            Arrays.asList("StringLiteral"), hex2Rgb("6A8759"), // Green
            Arrays.asList("IntegerLiteral", "FloatingPointLiteral"), hex2Rgb("6897BB"), // Blue
            Arrays.asList("'_'", "'('", "')'", "'{'", "'}'", "'['", "']'", "';'", "','", "'.'", "'...'", "'@'", "'::'", "'='", "'>'", "'<'", "'!'", "'~'", "'?'", "':'", "'->'", "'=='", "'<='", "'>='", "'!='", "'&&'", "'||'", "'++'", "'--'", "'+'", "'-'", "'*'", "'/'", "'&'", "'|'", "'^'", "'%'", "'+='", "'-='", "'*='", "'/='", "'&='", "'|='", "'^='", "'%='", "'<<='", "'>>='", "'>>>='", "Identifier", "WS"), Color.BLACK, // Black
            Arrays.asList("COMMENT", "LINE_COMMENT"), hex2Rgb("808080") // Grey
    );

    public void highlight(ScannedImage scannedImage) {
        try {
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
                var color = getColor(lex.getTokenNames()[token.getType()]);

                var line = scannedImage.getLine(token.getLine() - 1);
                for (int i = from; i < to; i++) {

                    var letter = line.get(i);

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

    private Color getColor(String tokenName) {
        return this.tokenMap.entrySet().stream().filter(entry -> entry.getKey().contains(tokenName)).findFirst().map(Map.Entry::getValue).orElseThrow(() -> new RuntimeException("No color found for token " + tokenName));
    }

    public static Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf( colorStr.substring( 0, 2 ), 16 ),
                Integer.valueOf( colorStr.substring( 2, 4 ), 16 ),
                Integer.valueOf( colorStr.substring( 4, 6 ), 16 ) );
    }

}
