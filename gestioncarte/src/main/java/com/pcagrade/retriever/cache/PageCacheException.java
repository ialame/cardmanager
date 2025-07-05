package com.pcagrade.retriever.cache;

import com.pcagrade.retriever.PCAException;

public class PageCacheException extends PCAException {

    public PageCacheException() {
        super();
    }

    public PageCacheException(String message) {
        super(message);
    }

    public PageCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public PageCacheException(Throwable cause) {
        super(cause);
    }

    protected PageCacheException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
