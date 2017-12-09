package com.uddernetworks.mspaint.main;

import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class LetterRow implements Serializable {

    private static final long serialVersionUID = 5L;
    private AtomicReferenceArray<Letter> row;

    public LetterRow(int width) {
        row = new AtomicReferenceArray<>(width);
    }

    private int lastIndex = 0;
    public void insertIn(Letter letter) {
        row.set(lastIndex, letter);
        lastIndex++;
    }

    public void sort() {
        System.out.println("row = " + row);

        AtomicReferenceArray<Letter> sorted = new AtomicReferenceArray<>(row.length());

        int lastIndex = 0;

        final int len = sorted.length();
        for (int i = 0; i < len; i++) {
            int minIndex = min();
            if (minIndex == -1) continue;

            Letter letter = row.get(minIndex);

            int currentX = letter.getX();

            int diff = currentX - lastIndex;

            int spaces = diff % 7;

            for (int i2 = 0; i2 < spaces; i2++) {
//                sorted.set(i + i2, new Letter(" ", 7, 21, lastIndex + (i), letter.getY(), false));
            }

            lastIndex = currentX;

            sorted.set(i, letter);
            row.set(minIndex, null);
        }

        System.out.println("sorted = " + sorted);

        row = sorted;
    }

    /*










    FOR TOMORROW THE 9TH::

    SORT

    INSERT SPACES

    SORT AGAIN





















     */

//    public void insertSpaces() {
//        int lastIndex = 0;
//
//        for (int i = 0; i < row.length(); i++) {
//            int currentX =
//        }
//    }

    public Letter get(int x) {
        for (int i = 0; i < row.length(); i++) {
            Letter temp = row.get(i);
            if (temp != null && temp.getX() == x) return temp;
        }
        return null;
    }

    private int compressedLength() {
        int len = 0;
        for (int i = 0; i < row.length(); i++) {
            Letter tempLetter = row.get(i);
            if (tempLetter == null) continue;
            if (tempLetter.getLetter().equals("")) continue;
            len++;
        }

        System.out.println("len = " + len);
        return len;
    }

    public int length() {
        return row.length();
    }

    private int min() {
        int min = -1;
        int minIndex = -1;

        for (int i = 0; i < row.length(); i++) {
            Letter tempLetter = row.get(i);
            if (tempLetter == null) continue;
            if (tempLetter.getLetter().equals("")) continue;

            int tempX = tempLetter.getX();

            if (tempX < min || min == -1) {
                min = tempX;
                minIndex = i;
            }
        }

        return minIndex;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < row.length(); i++) {
            Letter letter = row.get(i);
            if (letter != null) {
                ret.append(letter);
            }
        }

        ret.append(row.toString()).append("\n");

        return ret.toString();
    }
}
