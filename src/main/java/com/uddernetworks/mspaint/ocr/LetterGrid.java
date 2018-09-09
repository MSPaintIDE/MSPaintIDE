package com.uddernetworks.mspaint.ocr;

import com.uddernetworks.mspaint.main.Letter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class LetterGrid implements Serializable {

    private static final long serialVersionUID = 1L;
    private AtomicReferenceArray<LetterRow> letterGrid;
    private ExecutorService executor = Executors.newFixedThreadPool(25);

    public LetterGrid(int width, int height) {
        letterGrid = new AtomicReferenceArray<>(height);
        for (int i = 0; i < height; i++) {
            letterGrid.set(i, new LetterRow(width));
        }
    }

    public void addLetter(Letter letter) {
        int x = letter.getX();
        letterGrid.get(letter.getY()).insertIn(letter);

        for (int i = 0; i < letter.getWidth(); i++) {
            Letter replacing = letterGrid.get(letter.getY()).get(i + x + 1);
            if (replacing == null) {
                letterGrid.get(letter.getY()).insertIn(new Letter("", 10, 21, i + x + 1, letter.getY()));
            }
        }
    }

    public void compact() {
        AtomicInteger width = new AtomicInteger();
        AtomicInteger height = new AtomicInteger();

        for (int i = 0; i < letterGrid.length(); i++) {
            LetterRow row = letterGrid.get(i);
            executor.execute(() -> {
                if (!isEmpty(row)) {
                    row.sort();
                    height.getAndIncrement();
                    int tempWidth = getWidth(row) + 20;
                    if (tempWidth > width.get()) width.set(tempWidth);
                }
            });
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ignored) {}

        AtomicReferenceArray<LetterRow> newGrid;

        newGrid = new AtomicReferenceArray<>(height.get());
        for (int i = 0; i < height.get(); i++) {
            newGrid.set(i, new LetterRow(width.get()));
        }

        int addedRow = 0;
        for (int i2 = 0; i2 < letterGrid.length(); i2++) {
            LetterRow row = letterGrid.get(i2);
            if (!isEmpty(row)) {
                int tempWidth = getWidth(row);
                LetterRow newRow = new LetterRow(row.length() - 1);

                int added = 0;
                int addedSpace = 0;
                int i = 0;
                while (added + 1 < tempWidth) {
                    if (added + addedSpace >= newRow.length()) break;
                    Letter letter = row.get(i);
                    if (letter != null) {
                        newRow.insertIn(letter);
                        added++;
                    }

                    i++;
                }

                newGrid.set(addedRow, newRow);
                addedRow++;
            }
        }

        letterGrid = newGrid;
    }

    private boolean isEmpty(LetterRow row) {
        if (row.length() == 0) return true;
        for (int i = 0; i < row.length(); i++) {
            if (row.get(i) != null) return false;
        }
        return true;
    }

    private int getWidth(LetterRow row) {
        int lastNonNull = 0;
        for (int i = 0; i < row.length(); i++) {
            if (row.get(i) != null) lastNonNull++;
        }
        return lastNonNull + 1;
    }

    public List<List<Letter>> getLetterGridArray() {
        List<List<Letter>> ret = new ArrayList<>();

        for (int i = 0; i < letterGrid.length(); i++) {
            LetterRow row = letterGrid.get(i);

            ret.add(row.toList());
        }

        return ret;
    }

    public void trimLeft() {
        int minSpace = Integer.MAX_VALUE;

        for (int i2 = 0; i2 < letterGrid.length(); i2++) {
            StringBuilder line = new StringBuilder();
            LetterRow row = letterGrid.get(i2);

            int iMax = row.length() - 1;
            if (iMax == -1) continue;

            for (int i = 0; ; i++) {
                boolean print = !String.valueOf(row.get(i)).equals("null");

                if (print) {
                    line.append(row.get(i).getLetter());
                }

                if (i == iMax) break;
            }

            minSpace = Math.min(minSpace, getLeadingSpaces(line.toString()));
        }

        for (int i2 = 0; i2 < letterGrid.length(); i2++) {
            LetterRow row = letterGrid.get(i2);
            row.trimLeft(minSpace);
        }
    }

    public String getPrettyString() {
        trimLeft();
        StringBuilder ret = new StringBuilder();

        for (int i2 = 0; i2 < letterGrid.length(); i2++) {
            LetterRow row = letterGrid.get(i2);

            int iMax = row.length() - 1;
            if (iMax == -1) continue;

            for (int i = 0; ; i++) {
                boolean print = !String.valueOf(row.get(i)).equals("null");

                if (print) ret.append(row.get(i).getLetter());

                if (i == iMax) break;
            }

            ret.append('\n');
        }

        return ret.toString();
    }

    private int getLeadingSpaces(String string) {
        return string.indexOf(string.trim());
    }

}
