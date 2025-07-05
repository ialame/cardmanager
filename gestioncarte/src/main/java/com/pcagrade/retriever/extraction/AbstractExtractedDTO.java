package com.pcagrade.retriever.extraction;

import com.github.f4b6a3.ulid.Ulid;
import io.swagger.v3.oas.annotations.media.Schema;

public abstract class AbstractExtractedDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid id;

    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }

}
