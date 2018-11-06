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

package com.uddernetworks.mspaint.languages.python;

/**
 * These are the various token types supported by JSyntaxPane.
 *
 * @author ayman
 */
public enum TokenType {

    OPERATOR(4), // Language operators
    DELIMITER(5), // Delimiters.  Constructs that are not necessarily operators for a language
    KEYWORD(6), // language reserved keywords
    KEYWORD2(7), // Other language reserved keywords, like C #defines
    IDENTIFIER(8), // identifiers, variable names, class names
    NUMBER(9),     // numbers in various formats
    STRING(10),     // String
    STRING2(12),    // For highlighting meta chars within a String
    COMMENT(13),    // comments
    COMMENT2(14),   // special stuff within comments
    REGEX(15),      // regular expressions
    REGEX2(16),     // special chars within regular expressions
    TYPE(17),       // Types, usually not keywords, but supported by the language
    TYPE2(18),      // Types from standard libraries
    TYPE3(19),      // Types for users
    DEFAULT(20),    // any other text
    WARNING(21),    // Text that should be highlighted as a warning
    ERROR(22);      // Text that signals an error

    private byte value;

    TokenType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return this.value;
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