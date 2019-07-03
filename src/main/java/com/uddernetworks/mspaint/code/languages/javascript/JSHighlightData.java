package com.uddernetworks.mspaint.code.languages.javascript;

import com.uddernetworks.mspaint.code.languages.HighlightData;
import com.uddernetworks.mspaint.code.languages.Language;
import com.uddernetworks.mspaint.code.lexer.javascript.JavaScriptLexer;
import com.uddernetworks.mspaint.code.lexer.javascript.JavaScriptParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

import java.util.Arrays;
import java.util.Map;

public class JSHighlightData extends HighlightData {

    {
        this.tokenMap = Map.of(
                Arrays.asList("'null'", "BooleanLiteral", "'break'", "'do'", "'instanceof'", "'typeof'", "'case'", "'else'", "'new'", "'var'", "'catch'", "'finally'", "'return'", "'void'", "'continue'", "'for'", "'switch'", "'while'", "'debugger'", "'function'", "'this'", "'with'", "'default'", "'if'", "'throw'", "'delete'", "'in'", "'try'", "'class'", "'enum'", "'extends'", "'super'", "'const'", "'export'", "'import'", "'implements'", "'let'", "'private'", "'public'", "'interface'", "'package'", "'protected'", "'static'", "'yield'"), 0xCC7832, // Orange
                Arrays.asList("StringLiteral", "TemplateStringLiteral"), 0x6A8759, // Green
                Arrays.asList("DecimalLiteral", "HexIntegerLiteral", "OctalIntegerLiteral", "OctalIntegerLiteral2", "BinaryIntegerLiteral"), 0x6897BB, // Blue
                Arrays.asList("0", "'['", "']'", "'('", "')'", "'{'", "'}'", "';'", "','", "'='", "'?'", "':'", "'...'", "'.'", "'++'", "'--'", "'+'", "'-'", "'~'", "'!'", "'*'", "'/'", "'%'", "'>>'", "'<<'", "'>>>'", "'<'", "'>'", "'<='", "'>='", "'=='", "'!='", "'==='", "'!=='", "'&'", "'^'", "'|'", "'&&'", "'||'", "'*='", "'/='", "'%='", "'+='", "'-='", "'<<='", "'>>='", "'>>>='", "'&='", "'^='", "'|='", "'=>'", "Identifier", "WhiteSpaces", "LineTerminator"), 0x000000, // Black
                Arrays.asList("MultiLineComment", "SingleLineComment"), 0x808080, // Gray
                Arrays.asList("HtmlComment", "CDataComment"), 0x666666, // Light Gray
                Arrays.asList("RegularExpressionLiteral"), 0x780078, // Dark Pink/Purple
                Arrays.asList("UnexpectedCharacter"), 0xFF0000 // Red
        );
    }

    public JSHighlightData(Language language) {
        super(language);
    }

    @Override
    public Lexer getLexer(CharStream inputStream) {
        return new JavaScriptLexer(inputStream);
    }

    @Override
    public Parser getParser(TokenStream input) {
        return new JavaScriptParser(input);
    }
}
