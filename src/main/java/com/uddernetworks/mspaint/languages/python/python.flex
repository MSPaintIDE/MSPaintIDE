/*
 * Copyright 2008 Ayman Al-Sairafi ayman.alsairafi@gmail.com
 * Modifications copyright (C) 2018 Adam Yarris
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *       at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uddernetworks.mspaint.languages.python;

import com.uwyn.jhighlight.highlighter.ExplicitStateHighlighter;

import java.io.Reader;
import java.io.IOException;

%%

%public
%class PythonHighlighter
%implements ExplicitStateHighlighter
%extends DefaultJFlexLexer
%final
%unicode
%char
%type Token


%{

    @Override
    public int yychar() {
        return yychar;
    }

    private static final byte PARAN     = 1;
    private static final byte BRACKET   = 2;
    private static final byte CURLY     = 3;

//    private static final byte TokenType.OPERATOR = 4; // 0x000000
//    private static final byte TokenType.DELIMITER = 5; // 0x000000
//    private static final byte TokenType.KEYWORD = 6; // 0x3333ee
//    private static final byte TokenType.KEYWORD2 = 7; // 0x3333ee
//    private static final byte TokenType.TYPE = 8; // 0x000000
//    private static final byte TokenType.TYPE2 = 9; // 0x000000
//    private static final byte TokenType.TYPE3 = 10; // 0x000000
//    private static final byte TokenType.STRING = 11; // 0xcc6600
//    private static final byte TokenType.STRING2 = 12; // 0xcc6600
//    private static final byte TokenType.NUMBER = 13; // 0x999933
//    private static final byte TokenType.REGEX = 14; // 0xcc6600
//    private static final byte TokenType.IDENTIFIER = 15; // 0x000000
//    private static final byte TokenType.COMMENT = 16; // 0x339933
//    private static final byte TokenType.COMMENT2 = 17; // 0x339933
//    private static final byte TokenType.DEFAULT = 18; // 0x000000
//    private static final byte TokenType.WARNING = 19; // 0xCC0000
//    private static final byte TokenType.ERROR = 20; // 0xCC0000

    public int getStyleCount() {
        return 20;
    }

    public byte getStartState() {
    	return YYINITIAL+1;
    }

    public byte getCurrentState() {
    	return (byte) (yystate()+1);
    }

    public void setState(byte newState) {
    	yybegin(newState-1);
    }

    public Token getNextToken() throws IOException { // USUALLY public byte getNextToken()
    	return yylex();
    }

    public int getTokenLength() {
    	return yylength();
    }

    public void setReader(Reader r) {
    	this.zzReader = r;
    }

    public PythonHighlighter() {}
%}

/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

WhiteSpace = {LineTerminator} | [ \t\f]+

/* comments */
Comment = "#" {InputCharacter}* {LineTerminator}?

/* identifiers */
Identifier = [a-zA-Z][a-zA-Z0-9_]*

/* integer literals */
DecIntegerLiteral = 0 | [1-9][0-9]*
DecLongLiteral    = {DecIntegerLiteral} [lL]

HexIntegerLiteral = 0 [xX] 0* {HexDigit} {1,8}
HexLongLiteral    = 0 [xX] 0* {HexDigit} {1,16} [lL]
HexDigit          = [0-9a-fA-F]

OctIntegerLiteral = 0+ [1-3]? {OctDigit} {1,15}
OctLongLiteral    = 0+ 1? {OctDigit} {1,21} [lL]
OctDigit          = [0-7]

/* floating point literals */
FloatLiteral  = ({FLit1}|{FLit2}|{FLit3}) {Exponent}? [fF]
DoubleLiteral = ({FLit1}|{FLit2}|{FLit3}) {Exponent}?

FLit1    = [0-9]+ \. [0-9]*
FLit2    = \. [0-9]+
FLit3    = [0-9]+
Exponent = [eE] [+-]? [0-9]+

/* string and character literals */
StringCharacter = [^\r\n\"\\]
SQStringCharacter = [^\r\n\'\\]

%state STRING, ML_STRING, SQSTRING, SQML_STRING

%%

<YYINITIAL> {

  /* keywords */
  "and"                          |
  "as"                           |
  "assert"                       |
  "break"                        |
  "class"                        |
  "continue"                     |
  "def"                          |
  "del"                          |
  "elif"                         |
  "else"                         |
  "except"                       |
  "exec"                         |
  "finally"                      |
  "for"                          |
  "from"                         |
  "global"                       |
  "if"                           |
  "import"                       |
  "in"                           |
  "is"                           |
  "lambda"                       |
  "not"                          |
  "or"                           |
  "pass"                         |
  "print"                        |
  "self"                         | /* not exactly keyword, but almost */
  "raise"                        |
  "return"                       |
  "try"                          |
  "while"                        |
  "with"                         |
  "yield"                        { return token(TokenType.KEYWORD); }

  /* Built-in Types*/
  "yield"                        |
  "Ellipsis"                     |
  "False"                        |
  "None"                         |
  "NotImplemented"               |
  "True"                         |
  "__import__"                   |
  "__name__"                     |
  "abs"                          |
  "apply"                        |
  "bool"                         |
  "buffer"                       |
  "callable"                     |
  "chr"                          |
  "classmethod"                  |
  "cmp"                          |
  "coerce"                       |
  "compile"                      |
  "complex"                      |
  "delattr"                      |
  "dict"                         |
  "dir"                          |
  "divmod"                       |
  "enumerate"                    |
  "eval"                         |
  "execfile"                     |
  "file"                         |
  "filter"                       |
  "float"                        |
  "frozenset"                    |
  "getattr"                      |
  "globals"                      |
  "hasattr"                      |
  "hash"                         |
  "help"                         |
  "hex"                          |
  "id"                           |
  "input"                        |
  "int"                          |
  "intern"                       |
  "isinstance"                   |
  "issubclass"                   |
  "iter"                         |
  "len"                          |
  "list"                         |
  "locals"                       |
  "long"                         |
  "map"                          |
  "max"                          |
  "min"                          |
  "object"                       |
  "oct"                          |
  "open"                         |
  "ord"                          |
  "pow"                          |
  "property"                     |
  "range"                        |
  "raw_input"                    |
  "reduce"                       |
  "reload"                       |
  "repr"                         |
  "reversed"                     |
  "round"                        |
  "set"                          |
  "setattr"                      |
  "slice"                        |
  "sorted"                       |
  "staticmethod"                 |
  "str"                          |
  "sum"                          |
  "super"                        |
  "tuple"                        |
  "type"                         |
  "unichr"                       |
  "unicode"                      |
  "vars"                         |
  "xrange"                       |
  "zip"                          {  return token(TokenType.TYPE);  }



  /* operators */

  "("                            { return token(TokenType.OPERATOR,  PARAN); }
  ")"                            { return token(TokenType.OPERATOR, -PARAN); }
  "{"                            { return token(TokenType.OPERATOR,  CURLY); }
  "}"                            { return token(TokenType.OPERATOR, -CURLY); }
  "["                            { return token(TokenType.OPERATOR,  BRACKET); }
  "]"                            { return token(TokenType.OPERATOR, -BRACKET); }
  "+"                            |
  "-"                            |
  "*"                            |
  "**"                           |
  "/"                            |
  "//"                           |
  "%"                            |
  "<<"                           |
  ">>"                           |
  "&"                            |
  "|"                            |
  "^"                            |
  "~"                            |
  "<"                            |
  ">"                            |
  "<="                           |
  ">="                           |
  "=="                           |
  "!="                           |
  "<>"                           |
  "@"                            |
  ","                            |
  ":"                            |
  "."                            |
  "`"                            |
  "="                            |
  ";"                            |
  "+="                           |
  "-="                           |
  "*="                           |
  "/="                           |
  "//="                          |
  "%="                           |
  "&="                           |
  "|="                           |
  "^="                           |
  ">>="                          |
  "<<="                          |
  "**="                          { return token(TokenType.OPERATOR); }

  /* string literal */
  \"{3}                          {
                                    yybegin(ML_STRING);
                                    tokenStart = yychar;
                                    tokenLength = 3;
                                 }

  \"                             {
                                    yybegin(STRING);
                                    tokenStart = yychar;
                                    tokenLength = 1;
                                 }

  \'{3}                          {
                                    yybegin(SQML_STRING);
                                    tokenStart = yychar;
                                    tokenLength = 3;
                                 }

  \'                             {
                                    yybegin(SQSTRING);
                                    tokenStart = yychar;
                                    tokenLength = 1;
                                 }

  /* numeric literals */

  {DecIntegerLiteral}            |
  {DecLongLiteral}               |

  {HexIntegerLiteral}            |
  {HexLongLiteral}               |

  {OctIntegerLiteral}            |
  {OctLongLiteral}               |

  {FloatLiteral}                 |
  {DoubleLiteral}                |
  {FloatLiteral}[jJ]             { return token(TokenType.NUMBER); }

  /* comments */
  {Comment}                      { return token(TokenType.COMMENT); }

  /* whitespace */
  {WhiteSpace}                   { }

  /* identifiers */
  {Identifier}                   { return token(TokenType.IDENTIFIER); }

  "$" | "?"                      { return token(TokenType.ERROR); }
}

<STRING> {
  \"                             {
                                     yybegin(YYINITIAL);
                                     // length also includes the trailing quote
                                     return token(TokenType.STRING, tokenStart, tokenLength + 1);
                                 }

  {StringCharacter}+             { tokenLength += yylength(); }

  \\[0-3]?{OctDigit}?{OctDigit}  { tokenLength += yylength(); }

  /* escape sequences */

  \\.                            { tokenLength += 2; }
  {LineTerminator}               { yybegin(YYINITIAL);  }
}

<STRING> {
  \"{3}                          {
                                     yybegin(YYINITIAL);
                                     // length also includes the trailing quote
                                     return token(TokenType.STRING, tokenStart, tokenLength + 3);
                                 }

  {StringCharacter}+             { tokenLength += yylength(); }

  \\[0-3]?{OctDigit}?{OctDigit}  { tokenLength += yylength(); }

  \"                             { tokenLength ++;  }

  /* escape sequences */

  \\.                            { tokenLength += 2; }
  {LineTerminator}               { tokenLength ++;  }
}

<STRING> {
  "'"                            {
                                     yybegin(YYINITIAL);
                                     // length also includes the trailing quote
                                     return token(TokenType.STRING, tokenStart, tokenLength + 1);
                                 }

  {SQStringCharacter}+           { tokenLength += yylength(); }

  \\[0-3]?{OctDigit}?{OctDigit}  { tokenLength += yylength(); }

  /* escape sequences */

  \\.                            { tokenLength += 2; }
  {LineTerminator}               { yybegin(YYINITIAL);  }
}

<STRING> {
  \'{3}                          {
                                     yybegin(YYINITIAL);
                                     // length also includes the trailing quote
                                     return token(TokenType.STRING, tokenStart, tokenLength + 3);
                                 }

  {SQStringCharacter}+           { tokenLength += yylength(); }

  \\[0-3]?{OctDigit}?{OctDigit}  { tokenLength += yylength(); }

  \'                             { tokenLength ++;  }

  /* escape sequences */

  \\.                            { tokenLength += 2; }
  {LineTerminator}               { tokenLength ++;  }
}

/* error fallback */
[^]|\n                             {  }
<<EOF>>                          { return null; }
