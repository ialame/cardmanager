package com.pcagrade.painter.image.storage;

import java.io.File;
import java.io.IOException;

public class ImageStorageSecurityHelper {

    private ImageStorageSecurityHelper() { }

    public static void checkFileLocation(File file, String storagePath) throws ImageStorageException {
        try {
            var path = file.getCanonicalPath();

            if (!path.startsWith(storagePath)) {
                throw new SecurityException("Invalid file location: " + path + ", it is not within the configured url");
            }
        } catch (IOException e) {
            throw new ImageStorageException(e);
        }
    }

}
