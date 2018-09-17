package com.uddernetworks.mspaint.languages.brainfuck;

import com.uddernetworks.mspaint.languages.LanguageHighlighter;
import com.uwyn.jhighlight.highlighter.ExplicitStateHighlighter;

public class BrainfuckLanguageHighlighter implements LanguageHighlighter {

    @Override
    public String getCssClass(int style) {
        switch (style) {
            case BrainfuckHighlighter.PLAIN_STYLE:
                return 0x000000 + ","; // plain (black)
            case BrainfuckHighlighter.BRACKET_STYLE:
                return 0x00c87f + ","; // bracket (blue-green)
            case BrainfuckHighlighter.MATH_OP_STYLE:
                return 0x000000 + ","; // math (black)
            case BrainfuckHighlighter.MOVE_STYLE:
                return 0x007c1f + ","; // move (green)
            case BrainfuckHighlighter.IO_STYLE:
                return 0x0021ff + ","; // i/o (blue)
            case BrainfuckHighlighter.COMMENT_STYLE:
                return 0x939393 + ","; // comment (gray)
            case BrainfuckHighlighter.INPUT_STYLE:
                return 0xff0000 + ","; // input (red)
            default:
                return null;
        }
    }

    @Override
    public ExplicitStateHighlighter getHighlighter() {
        return new BrainfuckHighlighter();
    }
}
