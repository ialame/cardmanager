package com.pcagrade.painter.common.image.card;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;

public interface ICardImageService {
    @Nonnull
    List<CardImageDTO> findAllByCardId(@Nullable Ulid cardId);

    @Nonnull
    List<PublicCardImageDTO> findAllPublicByCardId(@Nullable Ulid cardId);

    void saveCardImage(@Nonnull CardImageDTO dto);

    void saveCardImages(@Nonnull Iterable<CardImageDTO> dto) ;

    void deleteCardImage(@Nonnull Ulid cardId, @Nonnull Localization localization);
}
