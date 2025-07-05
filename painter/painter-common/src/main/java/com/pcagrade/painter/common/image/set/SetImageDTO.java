package com.pcagrade.painter.common.image.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;

public record SetImageDTO(
        Ulid setId,
        Localization localization,
        Ulid imageId
) {
}
