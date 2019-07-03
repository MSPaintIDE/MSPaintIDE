package com.uddernetworks.mspaint.code.languages.python;

import com.uddernetworks.code.lexer.python.Python3Lexer;
import com.uddernetworks.code.lexer.python.Python3Parser;
import com.uddernetworks.mspaint.code.languages.HighlightData;
import com.uddernetworks.mspaint.code.languages.Language;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

import java.util.Arrays;
import java.util.Map;

public class PythonHighlightData extends HighlightData {

    {
        this.tokenMap = Map.of(
                Arrays.asList("'def'", "'return'", "'raise'", "'from'", "'import'", "'as'", "'global'", "'nonlocal'", "'assert'", "'if'", "'elif'", "'else'", "'while'", "'for'", "'in'", "'try'", "'finally'", "'with'", "'except'", "'lambda'", "'or'", "'and'", "'not'", "'is'", "'None'", "'class'", "'yield'", "'del'", "'pass'", "'continue'", "'break'", "'async'", "'await'"), 0xCC7832, // Orange
                Arrays.asList("STRING", "STRING_LITERAL"), 0x6A8759, // Green
                Arrays.asList("'True'", "'False'", "BYTES_LITERAL", "DECIMAL_INTEGER", "OCT_INTEGER", "HEX_INTEGER", "BIN_INTEGER", "FLOAT_NUMBER", "IMAG_NUMBER", "NUMBER", "INTEGER"), 0x6897BB, // Blue
                Arrays.asList("NEWLINE", "NAME", "'.'", "'...'", "'*'", "'('", "')'", "','", "':'", "';'", "'**'", "'='", "'['", "']'", "'|'", "'^'", "'&'", "'<<'", "'>>'", "'+'", "'-'", "'/'", "'%'", "'//'", "'~'", "'{'", "'}'", "'<'", "'>'", "'=='", "'>='", "'<='", "'<>'", "'!='", "'@'", "'->'", "'+='", "'-='", "'*='", "'@='", "'/='", "'%='", "'&='", "'|='", "'^='", "'<<='", "'>>='", "'**='", "'//='"), 0x000000, // Black
                Arrays.asList(), 0x808080, // Gray
                Arrays.asList("<INVALID>", "SKIP_", "UNKNOWN_CHAR", "DEDENT"), 0xFF0000 // Red
        );
    }

    public PythonHighlightData(Language language) {
        super(language);
    }

    @Override
    public Lexer getLexer(CharStream inputStream) {
        return new Python3Lexer(inputStream);
    }

    @Override
    public Parser getParser(TokenStream input) {
        return new Python3Parser(input);
    }
}
