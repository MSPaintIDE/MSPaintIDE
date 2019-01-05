package com.uddernetworks.mspaint.code.highlighter;

import com.uddernetworks.newocr.ScannedImage;
import com.uddernetworks.newocr.character.ImageLetter;

import java.awt.*;
import java.util.List;

public class LetterFormatter {

    private ScannedImage scannedImage;

    public LetterFormatter(ScannedImage scannedImage) {
        this.scannedImage = scannedImage;
    }

    public void formatLetters(String text) {
        String[] lines = text.split("\n");

        for (int y = 0; y < lines.length; y++) {
//            if (scannedImage.getLine(y).size() <= y) continue;
            List<ImageLetter> colorWrapperRow = scannedImage.getLine(y);

            String line = lines[y].trim();

            String[] nums = line.split(",");

            for (int j = 0; j < nums.length; j++)
                colorWrapperRow.get(j).setData(new Color(Integer.valueOf(nums[j])));
        }
    }
}