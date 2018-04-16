package com.uddernetworks.mspaint.ocr;

import com.uddernetworks.mspaint.main.ImageUtil;
import com.uddernetworks.mspaint.main.Letter;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.Probe;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageCompare {

    private Map<String, BufferedImage> images;
    private int totalIterations;
    private AtomicInteger currentIterations;
    private MainGUI mainGUI;
    private final AtomicBoolean loading = new AtomicBoolean(true);

    public LetterGrid getText(File inputImage, File objectFile, MainGUI mainGUI, Map<String, BufferedImage> images, boolean useProbe, boolean readFromFile, boolean saveCaches) {
        this.mainGUI = mainGUI;
        this.images = images;

        if (readFromFile) {
            System.out.println("Image not changed since file changed, so using file...");
        } else {
            System.out.println("Image has changed since last write to data file, reading image...");
        }

        try {
            System.out.println("Image = " + inputImage.getAbsolutePath());
            BufferedImage image = ImageUtil.blackAndWhite(ImageIO.read(inputImage));

            objectFile.createNewFile();

            LetterGrid grid;

            if (!readFromFile) {
                grid = new LetterGrid(image.getWidth(), image.getHeight());

                mainGUI.setStatusText("Probing...");

                AtomicInteger waitingFor = new AtomicInteger(images.keySet().size());

                Probe probe = new Probe(image, images.get("p"));

                int startY = (useProbe) ? probe.sendInProbe() : 0;
                int iterByY = (useProbe) ? 25 : 1;

                mainGUI.setStatusText("Scanning image " + inputImage.getName() + "...");

                System.out.println("Total images: " + images.keySet().size());

                int imageHeight = image.getHeight();

                totalIterations = 0;
                currentIterations = new AtomicInteger(0);

                for (String identifier : images.keySet()) {
                    int diffHeight = imageHeight - images.get(identifier).getHeight() - startY;

                    totalIterations += diffHeight / iterByY;
                }

                Thread loadingBarThread = new Thread(() -> {
                    while (loading.get()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        mainGUI.updateLoading(currentIterations.get(), totalIterations);
                    }
                });

                loadingBarThread.start();

                ExecutorService executor = Executors.newFixedThreadPool(10);

                for (String identifier : images.keySet()) {
                    executor.execute(() -> {
                        try {
                            searchFor(grid, identifier, image, startY, iterByY);
                            waitingFor.getAndDecrement();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

                executor.shutdown();
                while (!executor.isTerminated()) {}

                while (true) {
                    if (waitingFor.get() == 0) {
                        break;
                    } else {
                        System.out.println("Waiting...");
                    }

                    Thread.sleep(1000);
                }

                if (saveCaches) {
                    mainGUI.setStatusText("Saving to cache file...");

                    FileOutputStream fos = new FileOutputStream(objectFile);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);

                    oos.writeObject(grid);

                    oos.close();
                    fos.close();
                }

                loading.set(false);
                loadingBarThread.join();
            } else {
                FileInputStream fi = new FileInputStream(objectFile);
                ObjectInputStream oi = new ObjectInputStream(fi);

                grid = (LetterGrid) oi.readObject();
            }

            mainGUI.setStatusText("Compacting and processing collected data...");

            mainGUI.setIndeterminate(true);

            grid.compact();

            mainGUI.setIndeterminate(false);

            return grid;

        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void searchFor(LetterGrid grid, String identifier, BufferedImage image, int startY, int iterYBy) {
        BufferedImage searching = images.get(identifier);

        int currentX = 0;
        int currentY = startY;

        while (currentY + searching.getHeight() <= image.getHeight()) {
            currentX = 0;
            while (currentX + searching.getWidth() <= image.getWidth()) {
                BufferedImage subImage = image.getSubimage(currentX, currentY, searching.getWidth(), searching.getHeight());

//                subImage = new BufferedImage(subImage.getColorModel(), subImage.copyData(null), subImage.getColorModel().isAlphaPremultiplied(), null);

                if (identifier.equals("\'")) {
                    int topRightX = currentX + searching.getWidth();
                    int topRightY = currentY;

                    boolean matches = true;

                    for (int i = 0; i < 3; i++) {
                        if (isInBounds(image, topRightX + i, topRightY)) {
                            Color color = new Color(image.getRGB(topRightX + i, topRightY));
                            if (!(color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0)) {
                                matches = false;
                            }
                        } else {
                            matches = false;
                        }
                    }

                    for (int i = 0; i < 2; i++) {
                        if (isInBounds(image, topRightX + i, topRightY + 1)) {
                            Color color = new Color(image.getRGB(topRightX + i, topRightY + 1));
                            if (!(color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0)) {
                                matches = false;
                            }
                        } else {
                            matches = false;
                        }
                    }

                    if (matches) {
                        currentX++;
                        continue;
                    }
                } else if (identifier.equals(".")) {
                    int bottomLeftX = currentX;
                    int bottomLeftY = currentY + searching.getHeight();

                    boolean matches = true;

                    if (isInBounds(image, bottomLeftX, bottomLeftY + 2)) {
                        Color color = new Color(image.getRGB(bottomLeftX, bottomLeftY + 2));
                        if ((color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0)) {
                            matches = false;
                        }
                    } else {
                        matches = false;
                    }

                    bottomLeftX++;

                    for (int i = 0; i < 3; i++) {
                        if (isInBounds(image, bottomLeftX + i, bottomLeftY + 2)) {
                            Color color = new Color(image.getRGB(bottomLeftX + i, bottomLeftY + 2));
                            if (!(color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0)) {
                                matches = false;
                            }
                        } else {
                            matches = false;
                        }
                    }

                    bottomLeftX = currentX;

                    if (matches) {
                        currentX++;
                        continue;
                    }

                    matches = true;

                    for (int i = 0; i < 4; i++) {
                        if (isInBounds(image, bottomLeftX + i, bottomLeftY + 2)) {
                            Color color = new Color(image.getRGB(bottomLeftX + i, bottomLeftY + 2));
                            if ((color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0)) {
                                matches = false;
                            }
                        } else {
                            matches = false;
                        }
                    }

                    if (!matches) {
                        currentX++;
                        continue;
                    }

                } else if (identifier.equals("f") || identifier.equals("t")) {
                    if (ImageUtil.equals(subImage, searching, Arrays.asList(new Point(0, 0), new Point(0, 1)))) {
                        System.out.println("Found letter: " + identifier);
                        grid.addLetter(new Letter(identifier, searching.getWidth(), searching.getHeight(), currentX, currentY));
                    }
                    currentX++;

                    continue;
                }

                if (ImageUtil.equals(subImage, searching)) {
                    System.out.println("Found letter: " + identifier);
                    grid.addLetter(new Letter(identifier, searching.getWidth(), searching.getHeight(), currentX, currentY));
                }
                currentX++;
            }

            currentIterations.incrementAndGet();
            currentY += iterYBy;
        }

    }

    private boolean isInBounds(BufferedImage image, int x, int y) {
        return x > 0 && y > 0 && image.getWidth() > x && image.getHeight() > y;
    }

}
