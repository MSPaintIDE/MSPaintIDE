package com.uddernetworks.mspaint.ocr;

import com.uddernetworks.mspaint.main.Letter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
        AtomicReferenceArray<Letter> sorted = new AtomicReferenceArray<>(row.length());

        int lastIndex = 0;
        int index = 0;

        while (true) {
            int minIndex = min();
            if (minIndex == -1) break;

            Letter letter = row.get(minIndex);

            int currentX = letter.getX();

            if (letter.getLetter().equals("") || letter.getLetter().equals(" ")) {
                row.set(minIndex, null);
                continue;
            }



            int diff = currentX - lastIndex;

            int spaces = diff / 7;

            if (diff >= 7) {
                for (int i2 = 0; i2 < spaces; i2++) {
                    sorted.set(index, new Letter(" ", 7, 21, lastIndex + i2, letter.getY()));
                    index++;
                }
            }

            lastIndex = currentX + letter.getWidth();

            sorted.set(index, letter);
            row.set(minIndex, null);

            index++;
        }

        row = sorted;

    }

    public Letter get(int x) {
        for (int i = 0; i < row.length(); i++) {
            Letter temp = row.get(i);
            if (temp != null && temp.getX() == x) return temp;
        }
        return null;
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

    public List<Letter> toList() {
        List<Letter> ret = new ArrayList<>();

        for (int i = 0; i < row.length(); i++) {
            ret.add(row.get(i));
        }

        return ret;
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
