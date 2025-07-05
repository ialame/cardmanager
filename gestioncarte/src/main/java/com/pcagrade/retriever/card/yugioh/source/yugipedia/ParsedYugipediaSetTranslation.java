package com.pcagrade.retriever.card.yugioh.source.yugipedia;

import com.pcagrade.mason.localization.Localization;

import java.time.LocalDate;

public record ParsedYugipediaSetTranslation(
        Localization localization,
        String name,
        String prefix,
        String pid,
        LocalDate releaseDate
) {
}
