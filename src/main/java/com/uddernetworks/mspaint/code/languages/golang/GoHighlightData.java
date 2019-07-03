package com.uddernetworks.mspaint.code.languages.golang;

import com.uddernetworks.code.lexer.golang.GolangLexer;
import com.uddernetworks.code.lexer.golang.GolangParser;
import com.uddernetworks.mspaint.code.languages.HighlightData;
import com.uddernetworks.mspaint.code.languages.Language;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

import java.util.Arrays;
import java.util.Map;

public class GoHighlightData extends HighlightData {

    {
        this.tokenMap = Map.of(
                Arrays.asList("'package'", "'import'", "'const'", "'type'", "'func'", "'var'", "'return'", "'break'", "'continue'", "'goto'", "'fallthrough'", "'defer'", "'if'", "'else'", "'switch'", "'case'", "'default'", "'select'", "'for'", "'range'", "'go'", "'interface'", "'map'", "'chan'", "'struct'", "KEYWORD", "BINARY_OP"), 0xCC7832, // Orange
                Arrays.asList("STRING_LIT"), 0x6A8759, // Green
                Arrays.asList("INT_LIT", "FLOAT_LIT", "IMAGINARY_LIT", "RUNE_LIT", "LITTLE_U_VALUE", "BIG_U_VALUE"), 0x6897BB, // Blue
                Arrays.asList( "'('", "')'", "'.'", "'='", "','", "'{'", "'}'", "'<-'", "'++'", "'--'", "'+'", "'-'", "'|'", "'^'", "'*'", "'/'", "'%'", "'<<'", "'>>'", "'&'", "'&^'", "':='", "';'", "':'", "'['", "']'", "'...'", "'||'", "'&&'", "'=='", "'!='", "'<'", "'<='", "'>'", "'>='", "'!'", "IDENTIFIER", "WS", "TERMINATOR", "0"), 0x000000, // Black
                Arrays.asList("COMMENT"), 0x808080 // Gray
        );
    }

    public GoHighlightData(Language language) {
        super(language);
    }

    @Override
    public Lexer getLexer(CharStream inputStream) {
        return new GolangLexer(inputStream);
    }

    @Override
    public Parser getParser(TokenStream input) {
        return new GolangParser(input);
    }
}
