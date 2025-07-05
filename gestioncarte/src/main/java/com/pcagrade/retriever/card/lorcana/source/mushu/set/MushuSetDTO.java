package com.pcagrade.retriever.card.lorcana.source.mushu.set;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public record MushuSetDTO(
        String key,
        String name
) {

    public static final Comparator<MushuSetDTO> CHANGES_COMPARATOR = Comparator.comparing(MushuSetDTO::key, StringUtils::compare)
            .thenComparing(MushuSetDTO::name, StringUtils::compare);
}
