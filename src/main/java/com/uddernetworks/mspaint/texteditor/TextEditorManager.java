package com.uddernetworks.mspaint.texteditor;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.main.LetterFileWriter;
import com.uddernetworks.mspaint.main.Main;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.ReflectionUtils;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import com.uddernetworks.mspaint.splash.Splash;
import com.uddernetworks.newocr.character.ImageLetter;
import com.uddernetworks.newocr.recognition.DefaultScannedImage;
import com.uddernetworks.newocr.recognition.ScannedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
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
    private Main headlessMain;
    private Thread savingThread;
    private final AtomicBoolean stopping = new AtomicBoolean(false);

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

        File backup = new File(MainGUI.APP_DATA, "opened\\backup");
        backup.mkdirs();

        File backupFile = new File(backup, "original_" + this.originalFile.getName());
        backupFile.createNewFile();
        Files.copy(this.originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Main: " + Thread.currentThread());
        Thread thread = Thread.currentThread();

        var file2 = new File("C:\\Users\\RubbaBoy\\AppData\\Local\\MSPaintIDE\\threrads\\shit.txt");
        file2.createNewFile();

        var num = new AtomicInteger();
        var threadd = new Thread(() -> {
            while (true) {
                System.out.println("Here!");

                System.out.println("Printing stack trace:");


                try {
                    ReflectionUtils.printThreadInfo(new PrintWriter(file2), "" + num.getAndIncrement());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        System.getProperties().forEach((key, val) -> {
            System.out.println(key + " = " + val);
        });

        System.getProperty("file.encoding","UTF-8");
        System.getProperty("sun.jnu.encoding","UTF-8");
        System.getProperty("file.encoding","UTF-8");
        threadd.setName("Printer");
        threadd.start();
//        System.out.println("Right here UPDATED:");
//        GraphicsEnvironment ge =
//                GraphicsEnvironment.getLocalGraphicsEnvironment();
//        Font ni = null;
//        try {
//            ni = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\Program Files (x86)\\MS Paint IDE\\runtime\\lib\\fonts\\comic.ttf"));
//        } catch (FontFormatException e) {
//            e.printStackTrace();
//        }
//        ge.registerFont(ni);
//
//        var metrics = Toolkit.getDefaultToolkit().getFontMetrics(new Font("Verdana", Font.PLAIN, 36));
//        System.out.println("REALLLL metrics = " + metrics);
//        System.out.println("Done!");

        this.imageFile = createImageFile();

        this.imageClass = new ImageClass(this.imageFile, mainGUI, this.headlessMain);

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
            } catch (IOException | InterruptedException ignored) {}
        })).start();

        initialProcess();
        if (!MainGUI.HEADLESS) mainGUI.setIndeterminate(false);
    }

    private static HashMap<Font, FontMetrics> fontmetrics = new HashMap<Font, FontMetrics>();


    public static FontMetrics getFontMetrics(Font font)
    {
        if (fontmetrics.containsKey(font))
        {
            return fontmetrics.get(font);
        }
        FontMetrics fm = createFontMetrics(font);
        fontmetrics.put(font, fm);
        return fm;
    }

    private static FontMetrics createFontMetrics(Font font)
    {
        System.out.println("About");
        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
        System.out.println("222");
        Graphics g = bi.getGraphics();
        System.out.println("333");
        FontMetrics fm = g.getFontMetrics(font);
        System.out.println("444");
        g.dispose();
        System.out.println("555");

        bi = null;
        return fm;
    }

    public ScannedImage generateLetterGrid(String text) throws ExecutionException, InterruptedException, IOException {
        var ocrManager = this.headlessMain.getOCRManager();
        System.out.println("imageClass = " + imageClass);
        ScannedImage scannedImage = new DefaultScannedImage(this.originalFile, null);
        LetterGenerator letterGenerator = new LetterGenerator();

        var size = SettingsManager.getSetting(Setting.EDIT_FILE_SIZE, Integer.class);

        var data = this.headlessMain.getOCRManager().getActiveFont().getDatabaseManager().getAllCharacterSegments().get();
        System.out.println("data = " + data);

        // Gets the space DatabaseCharacter used for the current font size from the database
        var spaceOptional = data.stream().filter(databaseCharacter -> databaseCharacter.getLetter() == ' ').findFirst();

        if (spaceOptional.isEmpty()) {
            LOGGER.error("Couldn't find space for size: " + size);
            return null;
        }

        var space = spaceOptional.get();

        double spaceRatio = space.getAvgWidth() / space.getAvgHeight();
        int characterBetweenSpace = (int) ((spaceRatio * size) / 3D);

        var centerPopulator = this.headlessMain.getCenterPopulator();
        System.out.println("BEFORE");
        CompletableFuture.runAsync(() -> {
            try {
                centerPopulator.generateCenters((int) size);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).get();
        System.out.println("DONE!!!");

        int x = 0;
        int y = 0;
        for (String textLine : text.split("\n")) {
            List<ImageLetter> line = new ArrayList<>();
            for (char cha : textLine.toCharArray()) {

                if (cha == ' ') {
                    x += Math.floor(spaceRatio * size) - characterBetweenSpace;
                    continue;
                }

                boolean[][] letterGrid = letterGenerator.generateCharacter(cha, (int) size, ocrManager.getActiveFont(), space);
                int center = (int) ((size/ 2D) - centerPopulator.getCenter(cha, (int) size));
//                int center = centerPopulator.getCenter(cha, size);

                System.out.println("center = " + center + " (Size = " + size + ")");
                ImageLetter letter = new ImageLetter(cha, 0, x, y + center, letterGrid[0].length, letterGrid.length, 0D, 0D, 0D);
                letter.setValues(letterGrid);
                letter.setData(Color.BLACK);
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

        int padding = SettingsManager.getSetting(Setting.EDIT_FILE_SIZE, Integer.class);

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
