package com.pcagrade.retriever.card.yugioh.source.yugipedia;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.yugioh.YuGiOhCardDTO;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetDTO;
import com.pcagrade.retriever.card.yugioh.set.translation.YuGiOhSetTranslationDTO;
import com.pcagrade.retriever.card.yugioh.source.official.pid.OfficialSitePidDTO;
import com.pcagrade.retriever.card.yugioh.translation.YuGiOhCardTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface YugipediaMapper {

    YuGiOhCardDTO mapToDTO(Map<Localization, YugipediaCard> cards);

    @AfterMapping
    default void afterMapToDTO(@MappingTarget YuGiOhCardDTO dto, Map<Localization, YugipediaCard> cards) {
        var localizations = cards.keySet();

        localizations.forEach(localization -> {
            var card = cards.get(localization);

            if (card == null) {
                return;
            }

            dto.getTranslations().put(localization, mapToDTO(card, localization));
        });
    }

    @Mapping(target = "available", constant = "true")
    @Mapping(target = "labelName", source = "card.name")
    @Mapping(target = "number", source = "card.number")
    YuGiOhCardTranslationDTO mapToDTO(YugipediaCard card, Localization localization);

    @Mapping(target = "yugipediaSets", source = "sets")
    @Mapping(target = "officialSitePids", ignore = true)
    YuGiOhSetDTO mapToDTO(ParsedYugipediaSet set);

    @AfterMapping
    default void afterMapToDTO(@MappingTarget YuGiOhSetDTO dto, ParsedYugipediaSet set) {
        dto.setOfficialSitePids(mapOfficialSitePids(set));
    }

    @Nonnull
    default List<OfficialSitePidDTO> mapOfficialSitePids(ParsedYugipediaSet set) {
        return new ArrayList<>(set.translations().values().stream()
                .filter(t -> StringUtils.isNotBlank(t.pid()))
                .map(t -> new OfficialSitePidDTO(t.pid(), t.localization()))
                .toList());
    }

    @Mapping(target = "available", constant = "true")
    @Mapping(target = "code", source = "prefix")
    YuGiOhSetTranslationDTO mapToDTO(ParsedYugipediaSetTranslation set);
}
