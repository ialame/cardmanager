package com.pcagrade.retriever.extraction.consolidation;

import com.pcagrade.retriever.PCAException;

public class ConsolidationException extends PCAException {

    public ConsolidationException() {
        super();
    }

    public ConsolidationException(String message) {
        super(message);
    }

    public ConsolidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsolidationException(Throwable cause) {
        super(cause);
    }

    protected ConsolidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
