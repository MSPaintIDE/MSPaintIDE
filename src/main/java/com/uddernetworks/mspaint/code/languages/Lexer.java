package com.uddernetworks.mspaint.code.languages;

import javax.swing.text.Segment;
import java.util.List;

public interface Lexer {
    /**
     * This is the only method a Lexer needs to implement.  It will be passed
     * a Reader, and it should return non-overlapping Tokens for each recognized token
     * in the stream.
     * @param segment Text to parse.
     * @param ofst offset to add to start of each token (useful for nesting)
     * @param tokens List of Tokens to be added.  This is done so that the caller creates the
     * appropriate List implementation and size.  The parse method just adds to the list
     */
    public void parse(Segment segment, int ofst, List<Token> tokens);
}
