package com.uddernetworks.mspaint.main.gui.window.search;

import com.uddernetworks.mspaint.main.LetterFileWriter;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.texteditor.TextEditorManager;
import com.uddernetworks.newocr.ScannedImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ReplaceManager {


    private MainGUI mainGUI;

    public ReplaceManager(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    public void replaceText(SearchResult searchResult, String text) throws IOException, ExecutionException, InterruptedException {
        ScannedImage scannedImage = searchResult.getScannedImage();

        String originalText = scannedImage.getPrettyString();
        String[] lines = originalText.split("\n");

        int lineNumber = searchResult.getLineNumber() - 1;
        String line = lines[lineNumber];
        StringBuilder newLine = new StringBuilder();
        int foundPos = searchResult.getFoundPosition();

        if (foundPos != 0) newLine.append(line, 0, foundPos);

        newLine.append(text);

        if (foundPos != text.length()) newLine.append(line, foundPos + searchResult.getImageLetters().size(), line.length());

        lines[lineNumber] = newLine.toString();

        scannedImage = new TextEditorManager(this.mainGUI.getMain()).generateLetterGrid(String.join("\n", lines));

        BufferedImage original = ImageIO.read(searchResult.getFile());

        BufferedImage bufferedImage = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);

        LetterFileWriter letterFileWriter = new LetterFileWriter(scannedImage, bufferedImage, searchResult.getFile());
        letterFileWriter.writeToFile();
    }
}
