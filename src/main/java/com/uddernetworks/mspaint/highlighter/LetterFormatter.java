package com.uddernetworks.mspaint.highlighter;

import com.uddernetworks.mspaint.main.Letter;

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
            if (letters.size() <= i) continue;
            List<Letter> letterRow = letters.get(i);

            String line = lines[i].trim();

            String[] nums = line.split(",");

            for (int j = 0; j < nums.length; j++)
                letterRow.get(j).setColor(new Color(Integer.valueOf(nums[j])));
        }
    }
}