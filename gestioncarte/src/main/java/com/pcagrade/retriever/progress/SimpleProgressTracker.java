package com.pcagrade.retriever.progress;

public class SimpleProgressTracker implements IProgressTracker {

    private String message;
    private int progress = 0;
    private int max = 0;

    public SimpleProgressTracker(String message) {
        this.message = message;
    }

    public SimpleProgressTracker(String message, int max) {
        this(message);
        this.max = max;
    }

    @Override
    public synchronized void restart(String message, int max) {
        this.progress = 0;
        this.max = max;
        this.message = message;
    }

    @Override
    public synchronized void makeProgress(int progress) {
        this.progress += progress;
    }

    @Override
    public int getProgress() {
        return this.progress;
    }

    @Override
    public int getMax() {
        return this.max;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
