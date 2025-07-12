package com.pcagrade.painter.common.image.card;

import com.github.f4b6a3.ulid.Ulid;

public record CardImageDTO(
        Ulid cardId,
        String localization,
        Ulid imageId,
        String fichier
) {
}