package com.uddernetworks.mspaint.languages.brainfuck;

import com.uddernetworks.mspaint.languages.LanguageHighlighter;
import com.uwyn.jhighlight.highlighter.ExplicitStateHighlighter;

public class BrainfuckLanguageHighlighter implements LanguageHighlighter {

    @Override
    public String getCssClass(int style) {
        switch (style) {
            case BrainfuckHighlighter.PLAIN_STYLE:
                return "000,000,000,"; // plain (black)
            case BrainfuckHighlighter.BRACKET_STYLE:
                return "000,200,127,"; // bracket (blue-green)
            case BrainfuckHighlighter.MATH_OP_STYLE:
                return "000,000,000,"; // math (black)
            case BrainfuckHighlighter.MOVE_STYLE:
                return "000,124,031,"; // move (green)
            case BrainfuckHighlighter.IO_STYLE:
                return "000,033,255,"; // i/o (blue)
            case BrainfuckHighlighter.COMMENT_STYLE:
                return "147,147,147,"; // comment (gray)
            case BrainfuckHighlighter.INPUT_STYLE:
                return "255,000,000,"; // input (red)
            default:
                return null;
        }
    }

    @Override
    public ExplicitStateHighlighter getHighlighter() {
        return new BrainfuckHighlighter();
    }
}
