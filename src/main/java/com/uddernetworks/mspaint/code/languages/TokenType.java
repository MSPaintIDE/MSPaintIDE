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

package com.uddernetworks.mspaint.code.languages;

/**
 * These are the various token types supported by JSyntaxPane.
 *
 * @author ayman
 */
public enum TokenType {

    OPERATOR(0x000000, 0), // Language operators
    DELIMITER(0x000000, 1), // Delimiters.  Constructs that are not necessarily operators for a language
    KEYWORD(0x3333ee, 0), // language reserved keywords
    KEYWORD2(0x3333ee, 3), // Other language reserved keywords, like C #defines
    IDENTIFIER(0x000000, 0), // identifiers, variable names, class names
    NUMBER(0x999933, 1),     // numbers in various formats
    STRING(0xcc6600, 0),     // String
    STRING2(0xcc6600, 1),    // For highlighting meta chars within a String
    COMMENT(0x339933, 2),    // comments
    COMMENT2(0x666666, 3),   // special stuff within comments
    REGEX(0xcc6600, 0),      // regular expressions
    REGEX2(0xcc6600, 0),     // special chars within regular expressions
    TYPE(0x000000, 2),       // Types, usually not keywords, but supported by the language
    TYPE2(0x000000, 1),      // Types from standard libraries
    TYPE3(0x000000, 3),      // Types for users
    DEFAULT(0x000000, 0),    // any other text
    WARNING(0xCC0000, 0),    // Text that should be highlighted as a warning
    ERROR(0xCC0000, 3),      // Text that signals an error
    BF_PLAIN_STYLE(0x000000, 0),
    BF_BRACKET_STYLE(0x00c87f, 0),
    BF_MATH_OP_STYLE(0x000000, 0),
    BF_MOVE_STYLE(0x007c1f, 0),
    BF_IO_STYLE(0x0021ff, 0),
    BF_COMMENT_STYLE(0x939393, 0),
    BF_INPUT_STYLE(0xff0000, 0);

    private int style;
    private int font;

    TokenType(int style, int font) {
        this.style = style;
        this.font = font;
    }

    public int getStyle() {
        return style;
    }

    public int getFont() {
        return this.font;
    }

    /**
     * Tests if the given token is a Comment Token.
     * @param t
     * @return
     */
    public static boolean isComment(Token t) {
        if (t != null && (t.type == COMMENT || t.type == COMMENT2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tests if the given token is a Keyword Token.
     * @param t
     * @return
     */
    public static boolean isKeyword(Token t) {
        if (t != null && (t.type == KEYWORD || t.type == KEYWORD2)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Tests if the given token is a String Token.
     * @param t
     * @return
     */
    public static boolean isString(Token t) {
        if (t != null && (t.type == STRING || t.type == STRING2)) {
            return true;
        } else {
            return false;
        }
    }
}