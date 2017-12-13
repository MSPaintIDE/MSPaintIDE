package com.uddernetworks.mspaint.ocr;

import com.uddernetworks.mspaint.main.ImageUtil;
import com.uddernetworks.mspaint.main.Letter;
import com.uddernetworks.mspaint.main.Probe;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageCompare {

    private Map<String, BufferedImage> images;

    public LetterGrid getText(File inputImage, File objectFile, Map<String, BufferedImage>images, boolean useProbe, boolean readFromFile) {
        this.images = images;

        if (readFromFile) {
            System.out.println("Image not changed since file changed, so using file...");
        } else {
            System.out.println("Image has changed since last write to data file, reading image...");
        }

        try {
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
                        System.out.println(identifier);
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

                if (ImageUtil.equals(subImage, searching)) {
                    grid.addLetter(new Letter(identifier, searching.getWidth(), searching.getHeight(), currentX, currentY));
                }
                currentX++;
            }
            currentY += iterYBy;
        }

        System.out.println("Checked " + (currentX * currentY));

    }

}
