package com.pcagrade.retriever.card.onepiece.source.official.id;

import com.pcagrade.mason.localization.Localization;

import java.util.Comparator;

public record OnePieceOfficialSiteIdDTO(
        int id,
        Localization localization
) {
    public static final Comparator<OnePieceOfficialSiteIdDTO> CHANGES_COMPARATOR = Comparator.comparingInt(OnePieceOfficialSiteIdDTO::id)
            .thenComparing(OnePieceOfficialSiteIdDTO::localization);
}
