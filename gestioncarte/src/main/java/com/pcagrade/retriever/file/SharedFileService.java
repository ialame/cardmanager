package com.pcagrade.retriever.file;

import com.pcagrade.painter.common.image.ImageHelper;
import jakarta.annotation.Nonnull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

@Service
public class SharedFileService {

    private static final Logger LOGGER = LogManager.getLogger(SharedFileService.class);

    private final String commonResourcePath;

    public SharedFileService(@Value("${retriever.common-resource.path}") String commonResourcePath) {
        this.commonResourcePath = new File(commonResourcePath).getAbsolutePath();
    }

    public void saveBase64(String path, String content) {
        save(path, Base64.getDecoder().decode(content));
    }

    public void save(String path, byte[] content) {
        var file = getFile(path);

        createParentFolder(file);
        try {
            Files.write(file.toPath(), content);
            logSave(file);
        } catch (IOException e) {
            throw new SharedFilesException(e);
        }
    }

    @Nonnull
    public File getFile(String path) {
        var file = new File(commonResourcePath, path);

        checkFileLocation(file);
        return file;
    }

    public void convertToRed(String sourcePath, String targetPath) {
        var sourceFile = getFile(sourcePath);
        var targetFile = getFile(targetPath);

        createParentFolder(targetFile);
        try {
            var image = ImageHelper.convertToRed(ImageIO.read(sourceFile));

            ImageIO.write(image, "png", targetFile);
            logSave(targetFile);
        } catch (IOException e) {
            throw new SharedFilesException(e);
        }
    }

    private void logSave(File file) {
        LOGGER.info("Saved file {}", file.getAbsolutePath());
    }

    private void createParentFolder(File file) {
        var folder = file.getParentFile();

        checkFileLocation(folder);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new SharedFilesException("Unable to create folder " + folder);
        }
    }

    private void checkFileLocation(File file) {
        var path = file.getAbsolutePath();

        if (!path.startsWith(commonResourcePath)) {
            throw new SecurityException("Invalid file location: " + path + ", it is not within the configured path");
        }
    }

}
