package com.pcagrade.retriever.card.yugioh.source.yugipedia.set;

import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public record YugipediaSetDTO(
        String url,
        Localization localization
) {
    public static final Comparator<YugipediaSetDTO> CHANGES_COMPARATOR = Comparator.comparing(YugipediaSetDTO::url, StringUtils::compare)
            .thenComparing(YugipediaSetDTO::localization);
}
