package com.pcagrade.painter.image.legacy;

import com.pcagrade.painter.PainterException;

public class LegacyImageRestorationException extends PainterException {
    public LegacyImageRestorationException() {
        super();
    }

    public LegacyImageRestorationException(String message) {
        super(message);
    }

    public LegacyImageRestorationException(String message, Throwable cause) {
        super(message, cause);
    }

    public LegacyImageRestorationException(Throwable cause) {
        super(cause);
    }

    protected LegacyImageRestorationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
