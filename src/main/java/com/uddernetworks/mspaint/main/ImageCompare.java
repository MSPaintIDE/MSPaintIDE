package com.uddernetworks.mspaint.main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageCompare {

    private static Map<String, BufferedImage> images;
    private static Map<String, Boolean> hangsDown;


    private static int maxLetterHeight = 21;

    public static void main(String[] args) {

        ImageIndex imageIndex = new ImageIndex("E:\\MSPaintIDE\\letters");
        Object[] objs = imageIndex.index();
        images = (Map<String, BufferedImage>) objs[0];
        hangsDown = (Map<String, Boolean>) objs[1];

        File inputImage = new File("E:\\MSPaintIDE\\images\\long_input.png");
        File writeImage = new File("E:\\MSPaintIDE\\images\\long_input_contrast.png");

//        File inputImage = new File("E:\\MSPaintIDE\\images\\letter_input.png");
//        File writeImage = new File("E:\\MSPaintIDE\\images\\letter_input_contrast.png");
//
        try {
            BufferedImage image = ImageIO.read(inputImage);

//            BufferedImage wImage = new BufferedImage(image.getWidth(), image.getHeight(), TYPE_INT_ARGB);

//            BufferedImage wImage = ImageUtil.blackAndWhite(image);
            BufferedImage wImage = image;

            ImageIO.write(wImage, "png", writeImage);

            File objectFile = new File("E:\\MSPaintIDE\\LetterGridObject.txt");
            objectFile.createNewFile();

            boolean readObjectFromFile = true; // TODO ::::::::: IMPORTANT

            final long start = System.currentTimeMillis();

            LetterGrid grid;

            if (!readObjectFromFile) {
                grid = new LetterGrid(wImage.getWidth(), wImage.getHeight());


                AtomicInteger waitingFor = new AtomicInteger(images.keySet().size());


                for (String identifier : images.keySet()) {
                    new Thread(() -> {
                        System.out.println(identifier);
                        searchFor(grid, identifier, wImage);
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

                // Write objects to file
                oos.writeObject(grid);

                oos.close();
                fos.close();
            } else {
                FileInputStream fi = new FileInputStream(objectFile);
                ObjectInputStream oi = new ObjectInputStream(fi);

                // Read objects
                grid = (LetterGrid) oi.readObject();
            }




            System.out.println("Finished scan in " + (System.currentTimeMillis() - start) + "ms");

            grid.compact();

            System.out.println(grid.getPrettyString());


        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void searchFor(LetterGrid grid, String identifier, BufferedImage image) {
        BufferedImage searching = images.get(identifier);

        int currentX = 0;
        int currentY = 0;

        while (currentY + searching.getHeight() <= image.getHeight()) {
            currentX = 0;
            while (currentX + searching.getWidth() <= image.getWidth()) {
                BufferedImage subImage = image.getSubimage(currentX, currentY, searching.getWidth(), searching.getHeight());

                if (ImageUtil.equals(subImage, searching)) {
                    grid.addLetter(new Letter(identifier, searching.getWidth(), searching.getHeight(), currentX, currentY, hangsDown.get(identifier)));
                }
                currentX++;
            }
            currentY++;
        }

        System.out.println("Checked " + (currentX * currentY));

    }

}
