package com.pcagrade.painter.common.image;

import com.github.f4b6a3.ulid.Ulid;

import java.time.Instant;

public record ImageDTO(
        Ulid id,
        String path,
        String source,
        Instant creationDate,
        boolean internal
) { }
