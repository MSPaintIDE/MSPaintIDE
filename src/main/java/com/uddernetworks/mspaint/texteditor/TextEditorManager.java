package com.uddernetworks.mspaint.texteditor;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.main.LetterFileWriter;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.StartupLogic;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import com.uddernetworks.mspaint.splash.Splash;
import com.uddernetworks.newocr.character.ImageLetter;
import com.uddernetworks.newocr.recognition.DefaultScannedImage;
import com.uddernetworks.newocr.recognition.ScannedImage;
import com.uddernetworks.newocr.train.ImageReadMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TextEditorManager {

    private static Logger LOGGER = LoggerFactory.getLogger(TextEditorManager.class);

    private File originalFile;
    private File imageFile;
    private ImageClass imageClass;
    private StartupLogic startupLogic;
    private Thread savingThread;
    private final AtomicBoolean stopping = new AtomicBoolean(false);

    // Doesn't actually manage text files, used for generation
    public TextEditorManager(StartupLogic startupLogic) {
        this.startupLogic = startupLogic;
    }

    public TextEditorManager(String file) throws IOException, InterruptedException, ExecutionException {
        this(new File(file), null);
    }

    public TextEditorManager(File file, MainGUI mainGUI) throws IOException, InterruptedException, ExecutionException {
        this.originalFile = file.getAbsoluteFile();

        if (MainGUI.HEADLESS) {
            this.startupLogic = new StartupLogic();
            this.startupLogic.headlessStart();
        } else {
            this.startupLogic = mainGUI.getStartupLogic();
            mainGUI.setIndeterminate(true);
        }

        File backup = new File(MainGUI.APP_DATA, "opened\\backup");
        backup.mkdirs();

        File backupFile = new File(backup, "original_" + this.originalFile.getName());
        backupFile.createNewFile();
        Files.copy(this.originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        System.out.println("StartupLogic: " + Thread.currentThread());

        this.imageFile = createImageFile();

        this.imageClass = new ImageClass(this.imageFile, mainGUI, this.startupLogic);
        var options = this.startupLogic.getOCRManager().getActions().getOptions();
        options.setImageReadMethod(ImageReadMethod.IMAGEIO_STREAM);

        (this.savingThread = new Thread(() -> {
            try {
                Path path = FileSystems.getDefault().getPath(this.imageFile.getParent());
                try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                    path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                    long last = System.currentTimeMillis();
                    while (!stopping.get()) {
                        WatchKey wk = watchService.take();
                        boolean found = false;
                        for (WatchEvent<?> event : wk.pollEvents()) {
                            Path changed = ((Path) event.context()).toAbsolutePath();
                            if (changed.toFile().getName().equals(this.imageFile.getName())) found = true;
                        }

                        if (found && (System.currentTimeMillis() - last) > 250) {
                            this.imageClass.scan();
                            Files.write(this.originalFile.toPath(), this.imageClass.getTrimmedText().getBytes());
                            last = System.currentTimeMillis();
                        }

                        if (!wk.reset()) {
                            LOGGER.info("Key has been unregistered");
                        }
                    }
                }
            } catch (IOException | InterruptedException ignored) {
                ignored.printStackTrace();
            }
        })).start();

        initialProcess();
        if (!MainGUI.HEADLESS) mainGUI.setIndeterminate(false);
    }

    public ScannedImage generateLetterGrid(String text) throws ExecutionException, InterruptedException {
        var ocrManager = this.startupLogic.getOCRManager();
        ScannedImage scannedImage = new DefaultScannedImage(this.originalFile, null);
        LetterGenerator letterGenerator = new LetterGenerator();

        int size = SettingsManager.getInstance().getSetting(Setting.EDIT_FILE_SIZE);

        var data = this.startupLogic.getOCRManager().getActiveFont().getDatabaseManager().getAllCharacterSegments().get();

        // Gets the space DatabaseCharacter used for the current font size from the database
        var spaceOptional = data.stream().filter(databaseCharacter -> databaseCharacter.getLetter() == ' ').findFirst();

        if (spaceOptional.isEmpty()) {
            LOGGER.error("Couldn't find space for size: " + size);
            return null;
        }

        var space = spaceOptional.get();

        double spaceRatio = space.getAvgWidth() / space.getAvgHeight();
        int characterBetweenSpace = (int) ((spaceRatio * size) / 3D);

        var centerPopulator = this.startupLogic.getCenterPopulator();
        try {
            centerPopulator.generateCenters(size);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int x = 0;
        int y = 0;
        for (String textLine : text.split("\n")) {
            List<ImageLetter> line = new ArrayList<>();
            for (char cha : textLine.toCharArray()) {
                if (cha == ' ') {
                    x += Math.floor(spaceRatio * size) - characterBetweenSpace;
                    continue;
                }

                System.out.println("=== " + cha + " ===");

                var letterGrid = letterGenerator.generateCharacter(cha, size, ocrManager.getActiveFont(), space);
                int center = centerPopulator.getCenter(cha, size);

                ImageLetter letter = new ImageLetter(cha, 0, x, y + center, letterGrid[0].length, letterGrid.length, 0D, 0D, 0D);
                letter.setValues(LetterGenerator.doubleToBooleanGrid(letterGrid));
                letter.setData(letterGrid);
                line.add(letter);

                x += letterGrid[0].length + characterBetweenSpace;
            }

            scannedImage.addLine(y, line);

            y += size + ((int) (size * 0.5D));
            x = 0;
        }

        return scannedImage;
    }

    private File createImageFile() throws IOException, ExecutionException, InterruptedException {
        File tempImage = new File(MainGUI.APP_DATA, "opened\\" + this.originalFile.getName() + ".png");
        tempImage.mkdirs();

        String text = new String(Files.readAllBytes(this.originalFile.toPath()));
        LOGGER.info("text = " + text);

        int padding = SettingsManager.getInstance().getSetting(Setting.EDIT_FILE_SIZE);

        BufferedImage image = new BufferedImage(600, 500, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, Color.WHITE.getRGB());
            }
        }

        ImageIO.write(image, "png", tempImage);

        ScannedImage letterGrid = generateLetterGrid(text);
        int[] coords = getBiggestCoordinates(letterGrid);
        applyPadding(letterGrid, padding, padding);

        BufferedImage bufferedImage = new BufferedImage(coords[0] + padding * 2, coords[1] + padding * 2, BufferedImage.TYPE_INT_ARGB);

        LetterFileWriter letterFileWriter = new LetterFileWriter(letterGrid, bufferedImage, tempImage);
        letterFileWriter.writeToFile();

        return tempImage;
    }

    private void initialProcess() throws IOException, InterruptedException {
        LOGGER.info("Processing");

        CompletableFuture.runAsync(Splash::end);

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("mspaint.exe \"" + this.imageFile.getAbsolutePath() + "\"");

        LOGGER.info("Opened MS Paint");
        process.waitFor();

        LOGGER.info("Closed MS Paint!");

        stopping.set(true);
        savingThread.interrupt();
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

        return new int[]{xCoord.get(), yCoord.get()};
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
