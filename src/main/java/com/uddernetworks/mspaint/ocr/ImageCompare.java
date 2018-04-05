package com.uddernetworks.mspaint.ocr;

import com.uddernetworks.mspaint.main.ImageUtil;
import com.uddernetworks.mspaint.main.Letter;
import com.uddernetworks.mspaint.main.Probe;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageCompare {

    private Map<String, BufferedImage> images;

    public LetterGrid getText(File inputImage, File objectFile, Map<String, BufferedImage> images, boolean useProbe, boolean readFromFile) {
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


                AtomicInteger waitingFor = new AtomicInteger(images.keySet().size());

                Probe probe = new Probe(image, images.get("p"));

                int startY = (useProbe) ? probe.sendInProbe() : 0;
                int iterByY = (useProbe) ? 25 : 1;

                for (String identifier : images.keySet()) {
                    new Thread(() -> {
//                        System.out.println(identifier);
                        searchFor(grid, identifier, image, startY, iterByY);
                        waitingFor.getAndDecrement();
                    }).start();
                }

                while (true) {
                    if (waitingFor.get() == 0) {
                        break;
                    } else {
                        System.out.println("Waiting...");
                    }

                    Thread.sleep(1000);
                }

                FileOutputStream fos = new FileOutputStream(objectFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                oos.writeObject(grid);

                oos.close();
                fos.close();
            } else {
                FileInputStream fi = new FileInputStream(objectFile);
                ObjectInputStream oi = new ObjectInputStream(fi);

                grid = (LetterGrid) oi.readObject();
            }

            grid.compact();

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

                    if (matches) {
                        currentX++;
                        continue;
                    }

                } else if (identifier.equals("f")) {
//                    int topRightX = currentX + searching.getWidth();
//                    int topRightY = currentY;

//                    boolean matches = true;

//                    subImage.setRGB(0, 0, Color.WHITE.getRGB());
//                    subImage.setRGB(0, 1, Color.WHITE.getRGB());

//                    for (int i = 0; i < 2; i++) {
//                        if (isInBounds(image, topRightX, topRightY + 1)) {
//
//                            Color color = new Color(image.getRGB(topRightX + i, topRightY));
//                            if (!(color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0)) {
//                                matches = false;
//                            }
//                        } else {
//                            matches = false;
//                        }
//                    }
//
//                    if (matches) {
//                        currentX++;
//                        continue;
//                    }



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
            currentY += iterYBy;
        }

    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private boolean isInBounds(BufferedImage image, int x, int y) {
        return x > 0 && y > 0 && image.getWidth() > x && image.getHeight() > y;
    }

}
