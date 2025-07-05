package com.pcagrade.retriever.card.yugioh.source.official.pid;

import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public record OfficialSitePidDTO(
    String pid,
    Localization localization
) {
    public static final Comparator<OfficialSitePidDTO> CHANGES_COMPARATOR = Comparator.comparing(OfficialSitePidDTO::pid, StringUtils::compare)
            .thenComparing(OfficialSitePidDTO::localization);
}
