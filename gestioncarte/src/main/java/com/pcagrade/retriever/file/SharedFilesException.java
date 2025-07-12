package com.pcagrade.retriever.file;

import com.pcagrade.retriever.PCAException;

public class SharedFilesException extends PCAException {

    public SharedFilesException() {
        super();
    }

    public SharedFilesException(String message) {
        super(message);
    }

    public SharedFilesException(String message, Throwable cause) {
        super(message, cause);
    }

    public SharedFilesException(Throwable cause) {
        super(cause);
    }

    protected SharedFilesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
