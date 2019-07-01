package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.code.lexer.Java9Lexer;
import com.uddernetworks.code.lexer.Java9Parser;
import com.uddernetworks.mspaint.code.languages.HighlightData;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

import java.util.Arrays;
import java.util.Map;

public class JavaHighlightData extends HighlightData {

    {
        this.tokenMap = Map.of(
                Arrays.asList("'open'", "'module'", "'requires'", "'exports'", "'to'", "'opens'", "'uses'", "'provides'", "'with'", "'transitive'", "'abstract'", "'assert'", "'boolean'", "'break'", "'byte'", "'case'", "'catch'", "'char'", "'class'", "'const'", "'continue'", "'default'", "'do'", "'double'", "'else'", "'enum'", "'extends'", "'final'", "'finally'", "'float'", "'for'", "'if'", "'goto'", "'implements'", "'import'", "'instanceof'", "'int'", "'interface'", "'long'", "'native'", "'new'", "'package'", "'private'", "'protected'", "'public'", "'return'", "'short'", "'static'", "'strictfp'", "'super'", "'switch'", "'synchronized'", "'this'", "'throw'", "'throws'", "'transient'", "'try'", "'void'", "'volatile'", "'while'", "BooleanLiteral", "CharacterLiteral", "'null'"), 0xCC7832, // Orange
                Arrays.asList("StringLiteral"), 0x6A8759, // Green
                Arrays.asList("IntegerLiteral", "FloatingPointLiteral"), 0x6897BB, // Blue
                Arrays.asList("'_'", "'('", "')'", "'{'", "'}'", "'['", "']'", "';'", "','", "'.'", "'...'", "'@'", "'::'", "'='", "'>'", "'<'", "'!'", "'~'", "'?'", "':'", "'->'", "'=='", "'<='", "'>='", "'!='", "'&&'", "'||'", "'++'", "'--'", "'+'", "'-'", "'*'", "'/'", "'&'", "'|'", "'^'", "'%'", "'+='", "'-='", "'*='", "'/='", "'&='", "'|='", "'^='", "'%='", "'<<='", "'>>='", "'>>>='", "Identifier", "WS"), 0x000000, // Black
                Arrays.asList("COMMENT", "LINE_COMMENT"), 0x808080 // Gray
        );
    }

    @Override
    public Lexer getLexer(CharStream inputStream) {
        return new Java9Lexer(inputStream);
    }

    @Override
    public Parser getParser(TokenStream input) {
        return new Java9Parser(input);
    }
}
