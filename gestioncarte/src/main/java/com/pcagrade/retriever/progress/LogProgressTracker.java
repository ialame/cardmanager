package com.pcagrade.retriever.progress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogProgressTracker extends SimpleProgressTracker {

    private static final Logger LOGGER = LogManager.getLogger(LogProgressTracker.class);

    public LogProgressTracker(String message) {
        super(message);
    }

    public LogProgressTracker(String message, int max) {
        super(message, max);
    }

    @Override
    public synchronized void makeProgress(int progress) {
        super.makeProgress(progress);
        LOGGER.info("Progress: {} {}/{}", this::getMessage, this::getProgress, this::getMax);
    }

}
