package com.pcagrade.painter.common.image.card;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;

public record CardImageDTO(
        Ulid cardId,
        Localization localization,
        Ulid imageId,
        String fichier
) {
}
