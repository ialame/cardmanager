package com.pcagrade.painter.common.image.card;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;

public record PublicCardImageDTO(
        Ulid cardId,
        Localization localization,
        String url
) {
}
