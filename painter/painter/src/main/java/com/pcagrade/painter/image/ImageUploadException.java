package com.pcagrade.painter.image;

import com.pcagrade.painter.PainterException;

public class ImageUploadException extends PainterException {
    public ImageUploadException() {
        super();
    }

    public ImageUploadException(String message) {
        super(message);
    }

    public ImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageUploadException(Throwable cause) {
        super(cause);
    }

    protected ImageUploadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
