package com.pcagrade.painter.image.storage;

import java.io.IOException;

public class ImageStorageException extends IOException {

    public ImageStorageException() {
        super();
    }

    public ImageStorageException(String message) {
        super(message);
    }

    public ImageStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageStorageException(Throwable cause) {
        super(cause);
    }
}
