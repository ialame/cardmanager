package com.pcagrade.painter.common.image.card;

import com.github.f4b6a3.ulid.Ulid;

public record PublicCardImageDTO(
        Ulid cardId,
        String localization,
        String url
) {
}
