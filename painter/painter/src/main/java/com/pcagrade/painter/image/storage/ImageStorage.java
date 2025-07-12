package com.pcagrade.painter.image.storage;

import jakarta.annotation.Nonnull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

@Component
public class ImageStorage {

    private static final Logger LOGGER = LogManager.getLogger(ImageStorage.class);

    private final String imageStoragePath;

    public ImageStorage(@Value("${painter.image.storage-path}") String imageStoragePath) {
        String path;

        try {
            path = new File(imageStoragePath).getCanonicalPath();
        } catch (IOException e) {
            path = imageStoragePath;
            LOGGER.warn("Failed to get canonical path for {}", imageStoragePath, e);
        }
        this.imageStoragePath = path;
    }

    public void saveBase64Image(String path, String content) throws ImageStorageException {
        saveImage(path, Base64.getDecoder().decode(content));
    }

    public void saveImage(String path, byte[] content) throws ImageStorageException {
        try {
            saveImage(path, ImageIO.read(new ByteArrayInputStream(content)));
        } catch (IOException e) {
            throw new ImageStorageException(e);
        }
    }

    public void saveImage(String path, BufferedImage image) throws ImageStorageException {
        saveImage(path, image, "png");
    }

    public void saveImage(String path, BufferedImage image, String extension) throws ImageStorageException {
        var file = getFile(path);

        createParentFolder(file);
        try {
            ImageIO.write(image, extension, file);
            LOGGER.debug("Saved file {}", file.getCanonicalPath());
        } catch (IOException e) {
            throw new ImageStorageException(e);
        }
    }

    @Nonnull
    public File getFile(String path) throws ImageStorageException {
        var file = new File(imageStoragePath, path);

        checkFileLocation(file);
        return file;
    }

    private void createParentFolder(File file) throws ImageStorageException {
        var folder = file.getParentFile();

        checkFileLocation(folder);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new ImageStorageException("Unable to create folder " + folder);
        }
    }

    private void checkFileLocation(File file) throws ImageStorageException {
        ImageStorageSecurityHelper.checkFileLocation(file, imageStoragePath);
    }
}
