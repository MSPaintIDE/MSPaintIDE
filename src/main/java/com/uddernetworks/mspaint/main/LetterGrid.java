package com.uddernetworks.mspaint.main;

import java.awt.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class LetterGrid implements Serializable {

    /*

    Example x values:
    - 0
    - 25
    - 50
    - 75
    - 100

     */

    private static final long serialVersionUID = 1L;
    private AtomicReferenceArray<LetterRow> letterGrid;
    private Letter space;

    public LetterGrid() {}

    public LetterGrid(int width, int height) {
        letterGrid = new AtomicReferenceArray<>(height);
        for (int i = 0; i < height; i++) {
            letterGrid.set(i, new LetterRow(width));
        }

        space = new Letter(" ", 7, 0, 0, 0, false);
    }

    public void addLetter(Letter letter) {
        int x = letter.getX();
        letterGrid.get(letter.getY()).insertIn(letter);

//        System.out.println("letterGrid = " + letterGrid);

        for (int i = 0; i < letter.getWidth(); i++) {
            Letter replacing = letterGrid.get(letter.getY()).get(i + x + 1);
            if (replacing == null) { // || replacing.getLetter().equals(" ") || replacing.getLetter().equals("")
                letterGrid.get(letter.getY()).insertIn(new Letter("", 10, 21, i + x + 1, letter.getY(), false));
            }
        }
    }

    public void compact() {
        int width = 0;
        int height = 0;

        System.out.println("Second line:");
        printOutNumber(2);

        for (int i = 0; i < letterGrid.length(); i++) {
            LetterRow row = letterGrid.get(i);
            if (!isEmpty(row)) {
                row.sort();
                height++;
                int tempWidth = getWidth(row) + 20;
                if (tempWidth > width) width = tempWidth;
            }
        }



        System.out.println("\nAfter sorting:");
        printOutNumber(2);

        System.out.println("Width = " + width);
        System.out.println("Height = " + height);

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
//                AtomicReferenceArray<Letter> newRow = new AtomicReferenceArray<>(tempWidth - 1);
                LetterRow newRow = new LetterRow(row.length() - 1);

                int added = 0;
                int addedSpace = 0;
                int i = 0;
                int subAmount = 0;
                while (added + 1 < tempWidth) {
                    if (added + addedSpace >= newRow.length()) {
                        System.out.println((added + addedSpace) + " >= " + newRow.length());
                        break;
                    }
                    Letter letter = row.get(i);
                    if (letter != null) {
//                        if (letter.getLetter().equals("")) {
//                            i++;
//                            addedSpace++;
//                            continue;
//                        }
                        newRow.insertIn(letter); // added + addedSpace + subAmount
                        added++;
                    } else {
                        subAmount++;
                    }
//                    else if (has7SpacesInARow(row, added + addedSpace)) {
//                        System.out.println("Got space = " + (added + addedSpace));
//                        newRow.set(added + addedSpace, space);
//                        addedSpace += 7;
////                        addedSpace++;
//                    }

//                    if (added == 0 && has7SpacesInARow(row, added + addedSpace)) {
//                        newRow.set(added + addedSpace, space);
//                        addedSpace += 7;
//                    }
                    i++;
                }

                newGrid.set(addedRow, newRow);
                System.out.println(newRow);
                addedRow++;
            }
        }

//        FileUtils.write(new File("E:\\MSPaintIDE\\array.txt"), letterGrid.toString());

//        System.out.println("newGrid = " + letterGrid);

        letterGrid = newGrid;
    }

    private void printOutNumber(int index) {
        for (int i2 = 0; i2 < letterGrid.length(); i2++) {
            LetterRow row = letterGrid.get(i2);
            if (!isEmpty(row)) {
                index--;
                if (index == 0) {
                    System.out.println(row);
                    return;
                }
            }
        }
    }


    private boolean has7SpacesInARow(AtomicReferenceArray<Letter> array, int startIndex) {
        if (startIndex + 7 > array.length()) return false;
        int spacesGotten = 0;
        int i = startIndex;
        while (spacesGotten < 7) {
            if (i >= array.length()) {
//                System.out.println("NO space!!");
                return false;
            }

            if (array.get(i) == null) {
                i++;
                spacesGotten++;
                continue;
            }

            if (array.get(i).getLetter().equals("")) {
                i++;
                continue;
            }

            if (!array.get(i).getLetter().equals(" ")) {
                return false;
            }

            spacesGotten++;
            i++;
        }

//        System.out.println("Got space! Spaces = " + spacesGotten);

        return true;


//        for (int i = startIndex; i < 7; i++) {
//            if (array.get(i) != null) {
////                if (!array.get(0).getLetter().equals("")) continue;
//                if (!array.get(i).getLetter().equals(" ") && !array.get(i).getLetter().equals("")) {
//                    System.out.println("No space at: " + startIndex);
//                    return false;
//                }
//            }
//        }

//        System.out.println("Inserting space at: " + startIndex);
//        return true;
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

                if (i == iMax) {
//                    System.out.println(ret.append(']').toString());
                    break;
                }
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

//    // Spaces are 7px wide
//
//    public void printOutLetters() {
//        for (Letter[] row : letterGrid) {
//            for (int i = 0; i < row.length; i++) {
//                Letter currLetter = row[i];
//                Letter nextLetter = row[i + 1];
//
//                int spaces = (nextLetter.getX() - currLetter.getX() - currLetter.getWidth()) / 7;
//
//                System.out.print(getSpaces(spaces));
//
//                System.out.print(currLetter.getLetter());
//            }
//            System.out.print("\n");
//        }
//    }

    private String getSpaces(int amount) {
        StringBuilder space = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            space.append(" ");
        }
        return space.toString();
    }

//    private int getYPosition(int rawpos) {
//        return rawpos / 25;
//    }

}
