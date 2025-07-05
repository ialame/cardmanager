package com.pcagrade.retriever.card.yugioh.source.ygoprodeck;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.yugioh.YuGiOhCardDTO;
import com.pcagrade.retriever.card.yugioh.YuGiOhFieldMappingService;
import com.pcagrade.retriever.card.yugioh.translation.YuGiOhCardTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public abstract class YgoProDeckMapper {

    @Autowired
    private YuGiOhFieldMappingService fieldMappingService;

    public abstract YuGiOhCardDTO mapToDTO(Map<Localization, YgoProDeckCard> cards, Map<Localization, YgoProDeckSet> sets);

    @AfterMapping
    protected void afterMapToDTO(@MappingTarget YuGiOhCardDTO dto, Map<Localization, YgoProDeckCard> cards, Map<Localization, YgoProDeckSet> sets) {
        var localizations = cards.keySet();

        localizations.forEach(localization -> {
            var card = cards.get(localization);
            var set = sets.get(localization);

            if (card == null || set == null) {
                return;
            }

            dto.getTranslations().put(localization, mapToDTO(card, set, localization));
        });
    }

    @Mapping(target = "available", constant = "true")
    @Mapping(target = "labelName", source = "card.name")
    @Mapping(target = "rarity", source = "set.setRarityCode")
    @Mapping(target = "number", source = "set.setCode")
    public abstract YuGiOhCardTranslationDTO mapToDTO(YgoProDeckCard card, YgoProDeckSet set, Localization localization);

    @AfterMapping
    protected void afterMapToDTO(@MappingTarget YuGiOhCardTranslationDTO dto, YgoProDeckSet set) {
        var number = dto.getNumber();
        var rarity = StringUtils.trimToEmpty(StringUtils.replaceChars(dto.getRarity(), "()", ""));

        if (StringUtils.isNotBlank(number)) {
            dto.setNumber(fieldMappingService.mapNumber(dto.getLocalization(), number));
        }
        if (StringUtils.isNotBlank(rarity)) {
            dto.setRarity(fieldMappingService.mapRarity(dto.getLocalization(), rarity));
        }
    }
}
