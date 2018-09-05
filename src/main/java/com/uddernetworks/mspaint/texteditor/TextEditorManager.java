package com.uddernetworks.mspaint.texteditor;

import com.uddernetworks.mspaint.main.ImageUtil;
import com.uddernetworks.mspaint.main.Letter;
import com.uddernetworks.mspaint.main.LetterFileWriter;
import com.uddernetworks.mspaint.ocr.ImageIndex;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextEditorManager {

    private File originalFile;
    private File imageFile;

    private Map<String, BufferedImage> images;
    private Map<String, Integer> widths = new HashMap<>();

    public TextEditorManager(String file) throws IOException, InterruptedException {
        this.originalFile = new File(file).getAbsoluteFile();

        ImageIndex imageIndex = new ImageIndex(new File(System.getProperties().getProperty("user.home"), "AppData\\Local\\MSPaintIDE\\letters"));
        this.images = imageIndex.index();

        this.images.forEach((letter, image) -> this.widths.put(letter, ImageUtil.getWidth(image)));

        this.imageFile = createImageFile();

        new Thread(() -> {
            try {
                Path path = FileSystems.getDefault().getPath(this.imageFile.getParent());
                try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                    path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                    while (true) {
                        WatchKey wk = watchService.take();
                        for (WatchEvent<?> event : wk.pollEvents()) {
                            //we only register "ENTRY_MODIFY" so the context is always a Path.
                            Path changed = ((Path) event.context()).toAbsolutePath();

                            if (changed.toFile().getName().equals(this.imageFile.getName())) {
                                System.out.println("Saved!");
                                break;
                            }
                        }

                        Thread.sleep(500);

                        if (!wk.reset()) {
                            System.out.println("Key has been unregistered");
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        initialProcess();
    }

    private List<List<Letter>> generateLetterGrid(String text) {
        List<List<Letter>> letterGrid = new ArrayList<>();

        int x = 0;
        int y = 0;
        int yGrid = 0;
        for (String line : text.split("\n")) {
            letterGrid.add(new ArrayList<>());
            for (char cha : line.toCharArray()) {
                String character = String.valueOf(cha);
                BufferedImage letter = this.images.get(character);
                if (letter == null) {
                    if (cha == ' ') {
                        x += 6;
                    } else if (cha == '\t') {
                        x += 6 * 4;
                    }

                    continue;
                }

                int width = this.widths.get(character);
                letterGrid.get(yGrid).add(new Letter(character,  width, letter.getHeight(), x, y));
                x += width + 2;
            }

            yGrid++;
            y += 21;
            x = 0;
        }

        return letterGrid;
    }

    private int[] getBiggestCoordinates(List<List<Letter>> letterGrid) {
        int x = 0;
        int y = 0;

        for (List<Letter> letters : letterGrid) {
            for (Letter letter : letters) {
                x = Math.max(letter.getX(), x);
                y = Math.max(letter.getY(), y);
            }
        }

        return new int[] {x, y};
    }

    private File createImageFile() throws IOException {
        File tempImage = new File(System.getProperties().getProperty("user.home"), "AppData\\Local\\MSPaintIDE\\opened\\" + this.originalFile.getName() + ".png");
        tempImage.mkdirs();

        String text = new String(Files.readAllBytes(this.originalFile.toPath()));

        List<List<Letter>> letterGrid = generateLetterGrid(text);
        int[] coords = getBiggestCoordinates(letterGrid);

        BufferedImage bufferedImage = new BufferedImage(coords[0] + 25, coords[1] + 25, BufferedImage.TYPE_INT_ARGB);

        LetterFileWriter letterFileWriter = new LetterFileWriter(letterGrid, bufferedImage, tempImage);
        letterFileWriter.writeToFile(images);

        return tempImage;
    }

    private void initialProcess() throws IOException, InterruptedException {
        System.out.println("Processing");

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("mspaint.exe \"" + this.imageFile.getAbsolutePath() + "\"");

        System.out.println("Opened MS Paint");
        System.out.println(process.waitFor());

        System.out.println("Closed MS Paint!");

        if (!this.imageFile.delete()) {
            Thread.sleep(3000);
            this.imageFile.delete();
        }

        System.exit(0);
    }
}
