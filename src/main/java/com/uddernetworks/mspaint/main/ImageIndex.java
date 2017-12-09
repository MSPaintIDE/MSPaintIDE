package com.uddernetworks.mspaint.main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageIndex {

    private String directory;

    public ImageIndex(String directory) {
        this.directory = directory;
    }

    public Object[] index() {
        Object[] ret = new Object[2];
        Map<String, BufferedImage> images = new HashMap<>();
        Map<String, Boolean> hangsDown = new HashMap<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(directory + File.separator + "index.txt"));

            // "a.png" "a"

            for (String line : lines) {
                String[] spaceSplit = line.split(" ");
                String filename = spaceSplit[0];
                filename = filename.substring(1, filename.length() - 1);

                String identifier = spaceSplit[1];
                identifier = identifier.substring(1, identifier.length() - 1);


                if (spaceSplit.length == 2) {
                    hangsDown.put(identifier, false);
                } else {
                    hangsDown.put(identifier, spaceSplit[2].equals("true"));
                }

//                images.put(identifier, ImageUtil.trimWhitespace(ImageUtil.blackAndWhite(ImageIO.read(new File(directory, filename)))));
//                images.put(identifier, ImageUtil.trimWhitespace((ImageIO.read(new File(directory, filename)))));
                images.put(identifier, (ImageIO.read(new File(directory, filename))));
            }

            System.out.println("Index keys:\n\t" + images.keySet());

        } catch (IOException e) {
            e.printStackTrace();
        }

        ret[0] = images;
        ret[1] = hangsDown;

        return ret;
    }

}
