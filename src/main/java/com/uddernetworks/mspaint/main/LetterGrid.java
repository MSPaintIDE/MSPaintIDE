package com.uddernetworks.mspaint.main;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class LetterGrid implements Serializable {

    private static final long serialVersionUID = 1L;
    private AtomicReferenceArray<LetterRow> letterGrid;

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
                letterGrid.get(letter.getY()).insertIn(new Letter("", 10, 21, i + x + 1, letter.getY(), false));
            }
        }
    }

    public void compact() {
        int width = 0;
        int height = 0;

        for (int i = 0; i < letterGrid.length(); i++) {
            LetterRow row = letterGrid.get(i);
            if (!isEmpty(row)) {
                row.sort();
                height++;
                int tempWidth = getWidth(row) + 20;
                if (tempWidth > width) width = tempWidth;
            }
        }

        AtomicReferenceArray<LetterRow> newGrid;

        newGrid = new AtomicReferenceArray<>(height);
        for (int i = 0; i < height; i++) {
            newGrid.set(i, new LetterRow(width));
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
            Letter letter = row.get(i);
            if (letter != null) return false;
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

    public String getPrettyString() {
        StringBuilder ret = new StringBuilder();

        for (int i2 = 0; i2 < letterGrid.length(); i2++) {
            LetterRow row = letterGrid.get(i2);

            int iMax = row.length() - 1;
            if (iMax == -1) continue;

            for (int i = 0; ; i++) {
                boolean print = !String.valueOf(row.get(i)).equals("null");

                if (print) ret.append(String.valueOf(row.get(i)));

                if (i == iMax) break;
            }

            ret.append('\n');
        }

        return ret.toString();
    }

    public void rawPrint() {
        for (int i2 = 0; i2 < letterGrid.length(); i2++) {
            LetterRow row = letterGrid.get(i2);

            int iMax = row.length() - 1;
            if (iMax == -1) continue;

            StringBuilder b = new StringBuilder();
            b.append('[');
            for (int i = 0; ; i++) {
                boolean print = !String.valueOf(row.get(i)).equals("null");

                if (print) b.append(String.valueOf(row.get(i)));

                if (i == iMax) {
                    System.out.println(b.append(']').toString());
                    break;
                }

                if (print) b.append(", ");
            }
        }
    }

}
