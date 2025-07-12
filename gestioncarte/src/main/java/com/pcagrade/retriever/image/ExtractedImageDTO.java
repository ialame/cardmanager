package com.pcagrade.retriever.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public record ExtractedImageDTO(
    Localization localization,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    String source,
    String url,
    boolean internal,
    @Nullable
    String base64Image
) {

    @JsonIgnore
    public int size() {
        return base64Image == null ? 0 : base64Image.length();
    }

    public boolean hasSource(String... sources) {
        return Arrays.stream(sources).anyMatch(s -> StringUtils.equalsIgnoreCase(s, this.source));
    }
}
