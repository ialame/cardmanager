package com.pcagrade.retriever.card.yugioh.source.yugipedia;

import com.pcagrade.retriever.card.yugioh.source.yugipedia.set.YugipediaSetDTO;
import com.pcagrade.mason.localization.Localization;

import java.util.List;
import java.util.Map;

public record ParsedYugipediaSet(
        String name,
        String type,
        String prefix,
        String mainSet,
        Map<Localization, ParsedYugipediaSetTranslation> translations,
        List<YugipediaSetDTO> sets,
        String link
) {
}
