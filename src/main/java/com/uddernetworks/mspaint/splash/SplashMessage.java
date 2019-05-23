package com.uddernetworks.mspaint.splash;

import com.uddernetworks.mspaint.main.StartupLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public enum SplashMessage {
    BLANK("", "blank.png"),
    SETTINGS("Loading settings...", "settings.png"),
    DATABASE("Loading database...", "database.png"),
    ADDING_LANGUAGES("Adding languages...", "languages.png"),
    GUI("Loading GUI...", "gui.png"),
    STARTING("Starting...", "starting.png");

    private static Logger LOGGER = LoggerFactory.getLogger(SplashMessage.class);

    private String message;
    private String imagePath;
    private BufferedImage image;

    SplashMessage(String message, String imagePath) {
        this.message = message;
        this.imagePath = imagePath;
    }

    public String getMessage() {
        return message;
    }

    public String getImagePath() {
        return imagePath;
    }

    public BufferedImage getImage() {
        if (this.image != null) return this.image;

        try {
            var parentOptional = StartupLogic.getJarParent();
            if (parentOptional.isEmpty()) return null;
            var imageLocation = new File(parentOptional.get(), "splash\\" + imagePath);
            imageLocation.mkdirs();
            this.image = ImageIO.read(imageLocation);
        } catch (IOException e) {
            LOGGER.error("Error reading image", e);
        }

        return this.image;
    }
}
