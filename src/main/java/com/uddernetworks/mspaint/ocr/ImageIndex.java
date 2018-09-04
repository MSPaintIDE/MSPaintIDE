package com.uddernetworks.mspaint.ocr;

import com.uddernetworks.mspaint.main.ImageUtil;
import org.apache.tika.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageIndex {

    private File directory;

    public ImageIndex(File directory) {
        try {
            this.directory = directory;

            File[] files = this.directory.listFiles();
            if (!this.directory.exists() || !this.directory.isDirectory() || files == null || files.length == 0) {
                System.out.println("Creating directory: " + this.directory);
                this.directory.mkdirs();

                for (String line : IOUtils.toString(getClass().getClassLoader().getResourceAsStream("letters")).split("\n")) {
                    Files.copy(getClass().getClassLoader().getResourceAsStream("letters\\" + line), Paths.get(this.directory.getAbsolutePath(), line), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, BufferedImage> index() {
        Map<String, BufferedImage> images = new HashMap<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(directory.getAbsolutePath() + File.separator + "index.txt"));

            for (String line : lines) {
                String[] spaceSplit = line.split(" ");
                String filename = spaceSplit[0];
                filename = filename.substring(1, filename.length() - 1);

                String identifier = spaceSplit[1];
                identifier = identifier.substring(1, identifier.length() - 1);

                images.put(identifier, ImageUtil.blackAndWhite(ImageIO.read(new File(directory, filename))));
            }

            System.out.println("Index keys:\n\t" + images.keySet());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return images;
    }

}
