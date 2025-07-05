package com.pcagrade.retriever.parser.wiki;

import com.pcagrade.retriever.PCAException;

public class WikiParsingException  extends PCAException {


    public WikiParsingException() {
        super();
    }

    public WikiParsingException(String message) {
        super(message);
    }

    public WikiParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public WikiParsingException(Throwable cause) {
        super(cause);
    }

    protected WikiParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
