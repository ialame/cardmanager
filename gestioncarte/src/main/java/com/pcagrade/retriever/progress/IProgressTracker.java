package com.pcagrade.retriever.progress;

import io.swagger.v3.oas.annotations.media.Schema;

public interface IProgressTracker {

    void restart(String message, int max);
    void makeProgress(int progress);

    default void makeProgress() {
        makeProgress(1);
    }

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    int getProgress();
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    int getMax();
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    String getMessage();
}
