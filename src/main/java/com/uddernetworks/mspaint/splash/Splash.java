package com.uddernetworks.mspaint.splash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class Splash {

    private static Logger LOGGER = LoggerFactory.getLogger(Splash.class);

    private static AtomicReference<SplashMessage> status = new AtomicReference<>(SplashMessage.BLANK);
    private static final AtomicReference<SplashScreen> splash = new AtomicReference<>();
    private static final double totalStatuses = 4;
    private static AtomicReference<Double> statusAt = new AtomicReference<>(-1D);

    private static void renderSplashFrame(Graphics2D graphics) {
        graphics.setColor(Color.BLACK);
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0,0,1000,600);
        graphics.setPaintMode();
        graphics.drawImage(status.get().getImage(), null, 62, 526);
    }

    private void setProgress(Graphics2D graphics, double percentage) {
        graphics.setColor(new Color(84, 114, 211));
        graphics.fillRect(57, 484, (int) Math.round(875D * percentage), 30);
    }

    public Splash() {
        CompletableFuture.runAsync(() -> {
            splash.set(SplashScreen.getSplashScreen());
            Graphics2D graphics;

            if (splash.get() == null || (graphics = splash.get().createGraphics()) == null) {
                LOGGER.error("SplashScreen.getSplashScreen() or graphics returned null");
                return;
            }

            while (SplashScreen.getSplashScreen().isVisible()) {
                renderSplashFrame(graphics);
                setProgress(graphics, statusAt.get() / totalStatuses);
                splash.get().update();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {}
            }
        });
    }

    public static void setStatus(SplashMessage status) {
        SplashScreen splash = Splash.splash.get();
        statusAt.getAndUpdate(num -> num + 1);
        if (splash != null) Splash.status.set(status);
    }

    public static void end() {
        if (splash.get() != null && splash.get().isVisible()) splash.get().close();
    }
}