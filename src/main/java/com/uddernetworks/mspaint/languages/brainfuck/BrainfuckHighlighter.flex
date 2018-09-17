package com.uddernetworks.mspaint.languages.brainfuck;

import com.uwyn.jhighlight.highlighter.ExplicitStateHighlighter;

import java.io.Reader;
import java.io.IOException;

%%

%class BrainfuckHighlighter
%implements ExplicitStateHighlighter

%unicode
%pack

%buffer 128

%public

%int

%{

	/* styles */

	public static final byte PLAIN_STYLE = 1;
	public static final byte BRACKET_STYLE = 2;
	public static final byte MATH_OP_STYLE = 3;
	public static final byte MOVE_STYLE = 4;
	public static final byte IO_STYLE = 5;
	public static final byte COMMENT_STYLE = 6;
	public static final byte INPUT_STYLE = 7;

	/* Highlighter implementation */

	public int getStyleCount() {
		return 9;
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

	public byte getNextToken() throws IOException {
		return (byte) yylex();
	}

	public int getTokenLength() {
		return yylength();
	}

	public void setReader(Reader r) {
		this.zzReader = r;
	}

	public BrainfuckHighlighter() {}
%}

WhiteSpace = [ \t\f]

Identifier = [:jletter:][:jletterdigit:]*

%state IN_INPUT

%%

<YYINITIAL> {

  "[" |
  "]" { return BRACKET_STYLE; }

  "+" |
  "-" { return MATH_OP_STYLE; }

  ">" |
  "<" { return MOVE_STYLE; }

  "." |
  "," { return IO_STYLE; }

  "!" { yybegin(IN_INPUT); return INPUT_STYLE; }

  \n |
  {Identifier} |
  {WhiteSpace} { return COMMENT_STYLE; }
}

<IN_INPUT> {
    [^\n] { return INPUT_STYLE; }
}

/* error fallback */

[^]|\n { return COMMENT_STYLE; }