/*
 * Copyright 2008 Ayman Al-Sairafi ayman.alsairafi@gmail.com
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

package com.uddernetworks.mspaint.code.languages.brainfuck;


import com.uddernetworks.mspaint.code.languages.Token;
import com.uddernetworks.mspaint.code.languages.TokenType;
import com.uddernetworks.mspaint.code.languages.DefaultJFlexLexer;
import com.uddernetworks.mspaint.code.languages.Lexer;

%%

%public
%class BrainfuckLexer
%extends DefaultJFlexLexer
%final
%unicode
%char
%type Token


%{
    /**
     * Create an empty lexer, yyrset will be called later to reset and assign
     * the reader
     */
    public BrainfuckLexer() {
        super();
    }

    @Override
    public int yychar() {
        return yychar;
    }

    private static final byte PARAN     = 1;
    private static final byte BRACKET   = 2;
    private static final byte CURLY     = 3;

%}

WhiteSpace = [ \t\f]

Identifier = [:jletter:][:jletterdigit:]*

%state IN_INPUT

%%

<YYINITIAL> {

  "[" |
  "]" { return token(TokenType.BF_BRACKET_STYLE); }

  "+" |
  "-" { return token(TokenType.BF_MATH_OP_STYLE); }

  ">" |
  "<" { return token(TokenType.BF_MOVE_STYLE); }

  "." |
  "," { return token(TokenType.BF_IO_STYLE); }

  "!" { yybegin(IN_INPUT); return token(TokenType.BF_INPUT_STYLE); }

  \n |
  {Identifier} |
  {WhiteSpace} { return token(TokenType.BF_COMMENT_STYLE); }
}

<IN_INPUT> {
    [^\n] { return token(TokenType.BF_INPUT_STYLE); }
}

/* error fallback */

[^]|\n { return token(TokenType.BF_COMMENT_STYLE); }


/* error fallback */
.|\n                             {  }
<<EOF>>                          { return null; }

