package com.pcagrade.painter.common.image.legacy;

public record RestoreLegacyImageRequest(
        String folder,
        String path,
        boolean internal
) { }
