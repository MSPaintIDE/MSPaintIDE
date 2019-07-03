package com.uddernetworks.mspaint.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Browse {

    private static Logger LOGGER = LoggerFactory.getLogger(Browse.class);

    public static void browse(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("An error occurred while browsing to " + url, e);
        }
    }

    public static void browse(URI uri) {
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException e) {
            LOGGER.error("An error occurred while browsing to " + uri, e);
        }
    }

    public static void browse(URL url) {
        try {
            Desktop.getDesktop().browse(url.toURI());
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("An error occurred while browsing to " + url, e);
        }
    }

}
