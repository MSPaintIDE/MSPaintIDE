package com.uddernetworks.mspaint.main.gui.window.search;

import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.ocr.ImageCompare;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.newocr.ScannedImage;

import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SearchManager {

    private MainGUI mainGUI;
    private ImageCompare imageCompare = new ImageCompare();

    public SearchManager(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    public List<SearchResult> searchProject(String text, String extension, boolean ignoreCase) {
        return searchDirectory(ProjectManager.getPPFProject().getFile().getParentFile(), text, extension, ignoreCase);
    }

    public List<SearchResult> searchDirectory(File directory, String text, String extension, boolean ignoreCase) {
        if (!directory.isDirectory()) return Collections.emptyList();
        return this.mainGUI.getMain().getFilesFromDirectory(directory, extension)
                .parallelStream()
                .map(file -> searchFile(file, text, ignoreCase))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<SearchResult> searchFile(File file, String text, boolean ignoreCase) {
        if (!file.isFile()) return Collections.emptyList();
        File objectFile = new File(MainGUI.LOCAL_MSPAINT, "global_cache\\" + file.getName().substring(0, file.getName().length() - 4) + "_cache.json");

        ScannedImage scannedImage = imageCompare.getText(file, objectFile, this.mainGUI, this.mainGUI.getMain(), true, true);
        AtomicInteger lineNumber = new AtomicInteger(0);
        return scannedImage.getGrid().values()
                .stream()
                .map(line -> {
                    lineNumber.incrementAndGet();
                    int textIndex = 0;
                    for (int i = 0; i < line.size(); i++) {
                        char current = line.get(i).getLetter();
                        char textCharacter = text.charAt(textIndex);
                        if (ignoreCase) {
                            current = Character.toLowerCase(current);
                            textCharacter = Character.toLowerCase(textCharacter);
                        }

                        if (textIndex > 0 && current != textCharacter) {
                            textIndex = 0;
                        }

                        if (current == textCharacter && ++textIndex >= text.length()) {
                            return new SearchResult(file, setAllColor(scannedImage, Color.BLACK), text, ignoreCase, line.subList(i - textIndex + 1, i + 1), line, i - textIndex + 1, lineNumber.get());
                        }
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ScannedImage setAllColor(ScannedImage scannedImage, Color color) {
        scannedImage.getGrid().values().stream().flatMap(Collection::stream).forEach(imageLetter -> imageLetter.setData(color));
        return scannedImage;
    }

}
