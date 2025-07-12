package com.pcagrade.painter.common.image.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;

public interface ISetImageService {
    @Nonnull
    List<SetImageDTO> findAllBySetId(@Nullable Ulid setId);

    @Nonnull
    List<PublicSetImageDTO> findAllPublicBySetId(@Nullable Ulid setId);

    void saveSetImage(@Nonnull SetImageDTO dto);

    void saveSetImages(@Nonnull Iterable<SetImageDTO> dto);

    void deleteSetImage(@Nonnull Ulid setId, @Nonnull Localization localization);
}
