package com.uddernetworks.mspaint.texteditor;

import com.uddernetworks.mspaint.main.ImageClass;
import com.uddernetworks.mspaint.main.LetterFileWriter;
import com.uddernetworks.mspaint.main.Main;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.settings.Setting;
import com.uddernetworks.mspaint.main.settings.SettingsManager;
import com.uddernetworks.newocr.FontBounds;
import com.uddernetworks.newocr.ScannedImage;
import com.uddernetworks.newocr.character.ImageLetter;
import com.uddernetworks.newocr.database.DatabaseCharacter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class TextEditorManager {

    private File originalFile;
    private File imageFile;
    private ImageClass imageClass;
    private Main headlessMain;
    private Thread savingThread;

    private static final FontBounds[] FONT_BOUNDS = {
            new FontBounds(0, 12),
            new FontBounds(13, 20),
            new FontBounds(21, 30),
            new FontBounds(31, 100),
    };

    // Doesn't actually manage text files, used for generation
    public TextEditorManager(Main main) {
        this.headlessMain = main;
    }

    public TextEditorManager(String file) throws IOException, InterruptedException, ExecutionException {
        this(new File(file), null);
    }

    public TextEditorManager(File file, MainGUI mainGUI) throws IOException, InterruptedException, ExecutionException {
        this.originalFile = file.getAbsoluteFile();

        if (MainGUI.HEADLESS) {
            this.headlessMain = new Main();
            this.headlessMain.headlessStart();
        } else {
            this.headlessMain = mainGUI.getMain();
            mainGUI.setIndeterminate(true);
        }

        File backup = new File(MainGUI.LOCAL_MSPAINT, "opened\\backup");
        backup.mkdirs();

        File backupFile = new File(backup, "original_" + this.originalFile.getName());
        backupFile.createNewFile();
        Files.copy(this.originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        this.imageFile = createImageFile();

        File objectDirectory = new File(MainGUI.LOCAL_MSPAINT, "global_cache");
        this.imageClass = new ImageClass(this.imageFile, objectDirectory, mainGUI, this.headlessMain, true, true);

        (this.savingThread = new Thread(() -> {
            try {
                Path path = FileSystems.getDefault().getPath(this.imageFile.getParent());
                try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                    path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                    long last = System.currentTimeMillis();
                    while (true) {
                        WatchKey wk = watchService.take();
                        boolean found = false;
                        for (WatchEvent<?> event : wk.pollEvents()) {
                            Path changed = ((Path) event.context()).toAbsolutePath();
                            if (changed.toFile().getName().equals(this.imageFile.getName())) found = true;
                        }

                        if (found && (System.currentTimeMillis() - last) > 250) {
                            this.imageClass.scan(new File(objectDirectory, this.imageFile.getName().substring(0, this.imageFile.getName().length() - 4) + "_cache.json"), false, true);
                            Files.write(this.originalFile.toPath(), this.imageClass.getText().getBytes());
                            last = System.currentTimeMillis();
                        }

                        if (!wk.reset()) {
                            System.out.println("Key has been unregistered");
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {}
        })).start();

        initialProcess();
        if (!MainGUI.HEADLESS) mainGUI.setIndeterminate(false);
    }

    public ScannedImage generateLetterGrid(String text) throws ExecutionException, InterruptedException {
        ScannedImage scannedImage = new ScannedImage();
        LetterGenerator letterGenerator = new LetterGenerator();

        double size = SettingsManager.getSetting(Setting.EDIT_FILE_SIZE, Integer.class) * 1.3333333D;
        List<DatabaseCharacter> databaseCharacters = this.headlessMain.getDatabaseManager().getAllCharacterSegments(matchNearestFontSize((int) size)).get();
        DatabaseCharacter space = databaseCharacters
                .stream()
                .filter(databaseCharacter -> databaseCharacter.getLetter() == ' ')
                .findFirst()
                .orElse(null);

        if (space == null) {
            System.err.println("Couldn't find space for size: " + size);
            return null;
        }

        CenterPopulator centerPopulator = new CenterPopulator();
        centerPopulator.generateCenters((int) size);

        double spaceRatio = space.getAvgWidth() / space.getAvgHeight();
        int characterBetweenSpace = (int) ((spaceRatio * size) / 3);

        int x = 0;
        int y = 0;
        for (String textLine : text.split("\n")) {
            List<ImageLetter> line = new ArrayList<>();
            for (char cha : textLine.toCharArray()) {
                boolean[][] letterGrid = letterGenerator.generateCharacter(cha, (int) size, space);
                int center = (int) ((size/ 2D) - centerPopulator.getCenter(cha, (int) size));

                ImageLetter letter = new ImageLetter(new DatabaseCharacter(cha), x, y + center, letterGrid[0].length, letterGrid.length, -1D, null);
                letter.setValues(letterGrid);
                letter.setData(Color.BLACK);
                line.add(letter);

                x += letterGrid[0].length + characterBetweenSpace;

                if (cha == ' ') x += spaceRatio * size;
            }

            scannedImage.addLine(y, line);

            y += size + ((int) (size * 0.5D));
            x = 0;
        }

        return scannedImage;
    }

    public static FontBounds matchNearestFontSize(int fontSize) {
        return Arrays.stream(FONT_BOUNDS).filter(fontBounds -> fontBounds.isInbetween(fontSize)).findFirst().get();
    }

    private File createImageFile() throws IOException, ExecutionException, InterruptedException {
        System.out.println("originalFile = " + originalFile.getAbsolutePath());
        System.out.println("TextEditorManager.createImageFile");
        File tempImage = new File(MainGUI.LOCAL_MSPAINT, "opened\\" + this.originalFile.getName() + ".png");
        tempImage.mkdirs();

        String text = new String(Files.readAllBytes(this.originalFile.toPath()));
        System.out.println("text = " + text);

        int padding = 12;

        BufferedImage image = new BufferedImage(600, 500, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, Color.WHITE.getRGB());
            }
        }

        ImageIO.write(image, "png", tempImage);

        ScannedImage letterGrid = generateLetterGrid(text);
        int[] coords = getBiggestCoordinates(letterGrid);
        System.out.println("coords = " + Arrays.toString(coords));
        applyPadding(letterGrid, padding, padding);

        BufferedImage bufferedImage = new BufferedImage(coords[0] + padding * 2, coords[1] + padding * 2, BufferedImage.TYPE_INT_ARGB);

        LetterFileWriter letterFileWriter = new LetterFileWriter(letterGrid, bufferedImage, tempImage);
        letterFileWriter.writeToFile();

        return tempImage;
    }

    private void initialProcess() throws IOException, InterruptedException {
        System.out.println("Processing");

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("mspaint.exe \"" + this.imageFile.getAbsolutePath() + "\"");

        System.out.println("Opened MS Paint");
        process.waitFor();

        System.out.println("Closed MS Paint!");

        savingThread.join();

        if (!this.imageFile.delete()) {
            Thread.sleep(3000);
            this.imageFile.delete();
        }

        if (MainGUI.HEADLESS) System.exit(0);
    }

    // Utility methods

    public static int[] getBiggestCoordinates(ScannedImage scannedImage) {
        AtomicInteger xCoord = new AtomicInteger();
        AtomicInteger yCoord = new AtomicInteger();

        scannedImage.getGrid().values().forEach(line -> {
            for (ImageLetter imageLetter : line) {
                xCoord.set(Math.max(imageLetter.getX() + imageLetter.getWidth(), xCoord.get()));
                yCoord.set(Math.max(imageLetter.getY() + imageLetter.getHeight(), yCoord.get()));
            }
        });

        return new int[] {xCoord.get(), yCoord.get()};
    }

    public static void applyPadding(ScannedImage scannedImage, int xAmount, int yAmount) {
        scannedImage.getGrid().values().forEach(line -> {
            for (ImageLetter imageLetter : line) {
                imageLetter.setX(imageLetter.getX() + xAmount);
                imageLetter.setY(imageLetter.getY() + yAmount);
            }
        });
    }
}
