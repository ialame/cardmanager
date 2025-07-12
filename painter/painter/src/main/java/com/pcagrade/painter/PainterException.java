package com.pcagrade.painter;

public class PainterException extends RuntimeException {
    public PainterException() {
        super();
    }

    public PainterException(String message) {
        super(message);
    }

    public PainterException(String message, Throwable cause) {
        super(message, cause);
    }

    public PainterException(Throwable cause) {
        super(cause);
    }

    protected PainterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
