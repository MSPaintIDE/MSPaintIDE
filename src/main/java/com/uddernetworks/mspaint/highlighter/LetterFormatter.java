package com.uddernetworks.mspaint.highlighter;

import com.uddernetworks.mspaint.main.Letter;
import com.uddernetworks.mspaint.ocr.LetterRow;

import java.awt.*;
import java.util.List;

public class LetterFormatter {

    private List<List<Letter>> letters;

    public LetterFormatter(List<List<Letter>> letters) {
        this.letters = letters;
    }

    public void formatLetters(String text) {
        String[] lines = text.split("\n");

        for (int i = 0; i < lines.length; i++) {
            List<Letter> letterRow = letters.get(i);

            String line = lines[i].substring(0, lines[i].length() - 1).trim();

            String[] nums = line.split(",");

            int index = 0;
            for (int i2 = 0; i2 < nums.length; i2++) {
                int r = Integer.valueOf(nums[i2]);
                i2++;
                int g = Integer.valueOf(nums[i2]);
                i2++;
                int b = Integer.valueOf(nums[i2]);

                letterRow.get(index).setColor(new Color(r, g, b));
                index++;
            }
        }
    }
}