package com.patronSingleton;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;

/**
 * Singleton service for screen capture (screenshots).
 * Ensures only one instance exists to avoid opening multiple capture resources.
 */
public final class ScreenCaptureService {

    private static volatile ScreenCaptureService instance;
    private static final Object LOCK = new Object();

    private final Robot robot;
    private int captureCounter;

    private ScreenCaptureService() throws AWTException {
        this.robot = new Robot();
        this.captureCounter = 0;
    }

    /**
     * Returns the unique instance of the screen capture service.
     * Thread-safe lazy initialization (double-checked locking).
     */
    public static ScreenCaptureService getInstance() {
        ScreenCaptureService ref = instance;
        if (ref == null) {
            synchronized (LOCK) {
                ref = instance;
                if (ref == null) {
                    try {
                        instance = ref = new ScreenCaptureService();
                    } catch (AWTException e) {
                        throw new IllegalStateException("No se pudo inicializar el servicio de captura de pantalla.", e);
                    }
                }
            }
        }
        return ref;
    }

    /**
     * Captures the full screen and returns the image.
     */
    public BufferedImage captureFullScreen() {
        Rectangle screenBounds = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenBounds);
    }

    /**
     * Captures a region of the screen.
     *
     * @param region the rectangle to capture (x, y, width, height)
     */
    public BufferedImage captureRegion(Rectangle region) {
        return robot.createScreenCapture(region);
    }

    /**
     * Saves the given image to a file in the specified directory.
     * Filename is generated with timestamp and counter (e.g. captura_2025-01-15_14-30-00_1.png).
     *
     * @param image     the captured image
     * @param directory target directory path (if null or empty, uses user home)
     * @return the path of the saved file, or null if save failed
     */
    public Path saveToFile(BufferedImage image, String directory) {
        if (image == null) {
            return null;
        }
        Path dir = (directory == null || directory.isBlank())
                ? Paths.get(System.getProperty("user.home"), "CapturasPantalla")
                : Paths.get(directory);

        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            System.err.println("Error al crear el directorio: " + dir + " - " + e.getMessage());
            return null;
        }

        captureCounter++;
        String fileName = String.format("captura_%s_%d.png",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")),
                captureCounter);
        Path filePath = dir.resolve(fileName);

        try {
            ImageIO.write(image, "png", filePath.toFile());
            return filePath;
        } catch (IOException e) {
            System.err.println("Error al guardar la imagen: " + e.getMessage());
            return null;
        }
    }

    /**
     * Captures the full screen and saves it to the default directory (user home / CapturasPantalla).
     *
     * @return the path of the saved file, or null if capture/save failed
     */
    public Path captureAndSave() {
        return captureAndSave(null);
    }

    /**
     * Captures the full screen and saves it to the given directory.
     *
     * @param directory target directory (null for default)
     * @return the path of the saved file, or null if capture/save failed
     */
    public Path captureAndSave(String directory) {
        BufferedImage image = captureFullScreen();
        return saveToFile(image, directory);
    }

    public int getCaptureCounter() {
        return captureCounter;
    }
}
