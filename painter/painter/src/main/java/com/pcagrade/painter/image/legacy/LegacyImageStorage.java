package com.pcagrade.painter.image.legacy;

import com.pcagrade.painter.image.storage.ImageStorageException;
import com.pcagrade.painter.image.storage.ImageStorageSecurityHelper;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Component
public class LegacyImageStorage {

    private static final Logger LOGGER = LogManager.getLogger(LegacyImageStorage.class);

    private final String imageStoragePath;

    public LegacyImageStorage(@Value("${painter.image.legacy-storage-path}") String imageStoragePath) {
        String path;

        try {
            path = new File(imageStoragePath).getCanonicalPath();
        } catch (IOException e) {
            path = imageStoragePath;
            LOGGER.warn("Failed to get canonical path for {}", imageStoragePath, e);
        }
        this.imageStoragePath = path;
    }

    @Nonnull
    public String findImagePath(String id) throws ImageStorageException {
        if (StringUtils.isBlank(id)) {
            return "";
        }

        try (var stream = Files.find(Path.of(imageStoragePath), Integer.MAX_VALUE, (path, attr) -> attr.isRegularFile() && StringUtils.containsIgnoreCase(path.getFileName().toString(), id))) {
            var file = stream.findFirst()
                    .map(Path::toFile);

            if (file.isPresent()) {
                checkFileLocation(file.get());

                var path = file.get().getCanonicalPath();

                return StringUtils.removeStart(path, imageStoragePath);
            }
            return "";
        } catch (IOException e) {
            throw new ImageStorageException(e);
        }
    }

    public Optional<File> getImage(String path) throws ImageStorageException {
        var file = new File(imageStoragePath, path);

        checkFileLocation(file);
        return Optional.of(file);
    }

    private void checkFileLocation(File file) throws ImageStorageException {
        ImageStorageSecurityHelper.checkFileLocation(file, imageStoragePath);
    }
}
