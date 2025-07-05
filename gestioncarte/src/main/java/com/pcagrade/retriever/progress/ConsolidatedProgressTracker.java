package com.pcagrade.retriever.progress;

import java.util.ArrayList;
import java.util.List;

public class ConsolidatedProgressTracker implements IProgressTracker {

    private final List<IProgressTracker> subTrackers = new ArrayList<>();

    private String message;

    public ConsolidatedProgressTracker(String message) {
        this.message = message;
    }

    @Override
    public void restart(String message, int max) {
        this.message = message;
        subTrackers.clear();
    }

    @Override
    public void makeProgress(int progress) {
        var oldProgress = -1;

        while (progress > 0 && oldProgress != progress) {
            for (var subTracker : subTrackers) {
                if (subTracker.getProgress() < subTracker.getMax()) {
                    int subProgress = Math.min(progress, subTracker.getMax() - subTracker.getProgress());

                    subTracker.makeProgress(subProgress);
                    oldProgress = progress;
                    progress -= subProgress;
                    break;
                }
            }
        }
    }

    @Override
    public int getProgress() {
        return subTrackers.stream()
                .mapToInt(IProgressTracker::getProgress)
                .sum();
    }

    @Override
    public int getMax() {
        return subTrackers.stream()
                .mapToInt(IProgressTracker::getMax)
                .sum();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public List<IProgressTracker> getSubTrackers() {
        return List.copyOf(subTrackers);
    }

    public IProgressTracker createSubTracker() {
        var subTracker = new SimpleProgressTracker(message);

        subTrackers.add(subTracker);
        return subTracker;
    }
}
