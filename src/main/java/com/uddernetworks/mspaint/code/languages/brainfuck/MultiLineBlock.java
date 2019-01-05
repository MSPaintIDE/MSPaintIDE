package com.uddernetworks.mspaint.code.languages.brainfuck;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MultiLineBlock {
    private List<String> lines;
    private int length;
    private int currentCharacter = -1;
    private int currentLine = 0;

    public MultiLineBlock(String string) {
        this.lines = Arrays.stream(string.split("\n")).collect(Collectors.toList());
        this.length = string.replace("\n", "").length();
    }

    public char getCharAt(int index) {
        final int originalCurrentChar = currentCharacter;
        final int originalCurrentLine = currentLine;

        currentCharacter = -1;
        currentLine = 0;

        char result = 0;

        int i = 0;
        while (canContinue()) {
            if (i++ == index) {
                result = getNext();
                break;
            }
        }

        currentCharacter = originalCurrentChar;
        currentLine = originalCurrentLine;

        return result;
    }

    public char getNext() {
        return lines.get(currentLine).charAt(currentCharacter);
    }

    public boolean canContinue() {
        if (lines.get(currentLine).length() <= ++currentCharacter) {
            currentCharacter = 0;
            currentLine++;
        } else {
            return true;
        }

        return lines.size() > currentLine;
    }

    public int length(){
        return this.length;
    }

    public int getCharacter() {
        return this.currentCharacter;
    }

    public int getLine() {
        return this.currentLine;
    }
}