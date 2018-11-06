package com.uddernetworks.mspaint.texteditor;

import com.uddernetworks.mspaint.main.ImageClass;
import com.uddernetworks.newocr.ScannedImage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class TextEditorManager {

    private File originalFile;
    private File imageFile;
    private ImageClass imageClass;

    public TextEditorManager(String file) throws IOException, InterruptedException {
        this.originalFile = new File(file).getAbsoluteFile();

        File localMSPaintIDE = new File(System.getProperties().getProperty("user.home"), "AppData\\Local\\MSPaintIDE");
        File backup = new File(localMSPaintIDE, "opened\\backup");
        backup.mkdirs();

        File backupFile = new File(backup, "original_" + this.originalFile.getName());
        backupFile.createNewFile();
        Files.copy(backupFile.toPath(), this.originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        this.imageFile = createImageFile();

        this.imageClass = new ImageClass(this.imageFile, null, null, false, true);

        new Thread(() -> {
            try {
                Path path = FileSystems.getDefault().getPath(this.imageFile.getParent());
                try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                    path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                    while (true) {
                        WatchKey wk = watchService.take();
                        for (WatchEvent<?> event : wk.pollEvents()) {
                            Path changed = ((Path) event.context()).toAbsolutePath();

                            if (changed.toFile().getName().equals(this.imageFile.getName())) {
                                Thread.sleep(500);
                                this.imageClass.scan(null, false, false);
                                System.out.println(this.imageClass.getText());
                                Files.write(this.originalFile.toPath(), this.imageClass.getText().getBytes());
                                break;
                            }
                        }

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

    // TODO: Implement
    private ScannedImage generateLetterGrid(String text) {
//        List<List<ColorWrapper>> letterGrid = new ArrayList<>();

//        int x = 0;
//        int y = 0;
//        int yGrid = 0;
//        for (String line : text.split("\n")) {
//            letterGrid.add(new ArrayList<>());
//            for (char cha : line.toCharArray()) {
//                String character = String.valueOf(cha);
//                BufferedImage letter = this.images.get(character);
//                if (letter == null) {
//                    if (cha == ' ') {
//                        x += 6;
//                    } else if (cha == '\t') {
//                        x += 6 * 4;
//                    }
//
//                    continue;
//                }
//
//                int width = this.widths.get(character);
//                letterGrid.get(yGrid).add(new ColorWrapper(character,  width, letter.getHeight(), x, y));
//                x += width + 2;
//            }
//
//            yGrid++;
//            y += 21;
//            x = 0;
//        }

//        return letterGrid;
        return null;
    }

//    private int[] getBiggestCoordinates(List<List<ColorWrapper>> letterGrid) {
//        int x = 0;
//        int y = 0;
//
//        for (List<ColorWrapper> colorWrappers : letterGrid) {
//            for (ColorWrapper colorWrapper : colorWrappers) {
//                x = Math.max(colorWrapper.getX(), x);
//                y = Math.max(colorWrapper.getY(), y);
//            }
//        }
//
//        return new int[] {x + 8, y + 26};
//    }
//
//    private void applyPadding(List<List<ColorWrapper>> letterGrid, int xAmount, int yAmount) {
//        for (List<ColorWrapper> colorWrappers : letterGrid) {
//            for (ColorWrapper colorWrapper : colorWrappers) {
//                colorWrapper.setX(colorWrapper.getX() + xAmount);
//                colorWrapper.setY(colorWrapper.getY() + yAmount);
//            }
//        }
//    }

    private File createImageFile() throws IOException {
        // TODO: Implement
        File tempImage = new File(System.getProperties().getProperty("user.home"), "AppData\\Local\\MSPaintIDE\\opened\\" + this.originalFile.getName() + ".png");
        tempImage.mkdirs();

        String text = new String(Files.readAllBytes(this.originalFile.toPath()));

        int padding = 12;

//        List<List<ColorWrapper>> letterGrid = generateLetterGrid(text);
//        int[] coords = getBiggestCoordinates(letterGrid);
//        applyPadding(letterGrid, padding, padding);

//        BufferedImage bufferedImage = new BufferedImage(coords[0] + padding * 2, coords[1] + padding * 2, BufferedImage.TYPE_INT_ARGB);

//        LetterFileWriter letterFileWriter = new LetterFileWriter(letterGrid, bufferedImage, tempImage);
//        letterFileWriter.writeToFile(images);

        return tempImage;
    }

    private void initialProcess() throws IOException, InterruptedException {
        System.out.println("Processing");

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("mspaint.exe \"" + this.imageFile.getAbsolutePath() + "\"");

        System.out.println("Opened MS Paint");
        process.waitFor();

        System.out.println("Closed MS Paint!");

        if (!this.imageFile.delete()) {
            Thread.sleep(3000);
            this.imageFile.delete();
        }

        System.exit(0);
    }
}
