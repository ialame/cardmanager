package com.pcagrade.retriever.card.yugioh.source.official;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.yugioh.YuGiOhCardDTO;
import com.pcagrade.retriever.card.yugioh.serie.YuGiOhSerieDTO;
import com.pcagrade.retriever.card.yugioh.serie.translation.YuGiOhSerieTranslationDTO;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetDTO;
import com.pcagrade.retriever.card.yugioh.set.translation.YuGiOhSetTranslationDTO;
import com.pcagrade.retriever.card.yugioh.set.translation.YuGiOhSetTranslationMapper;
import com.pcagrade.retriever.card.yugioh.source.official.pid.OfficialSitePidDTO;
import com.pcagrade.retriever.card.yugioh.translation.YuGiOhCardTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        uses = {YuGiOhSetTranslationMapper.class})
public interface YuGiOhOfficialSiteMapper {

    @Mapping(target = "translations", source = "cards")
    YuGiOhCardDTO mapToDTO(OfficialSiteCard mainCard, List<OfficialSiteCard> cards);

    default YuGiOhCardDTO mapToDTO(List<OfficialSiteCard> cards) {
        if(CollectionUtils.isEmpty(cards)) {
            return null;
        }

        var mainSet = cards.stream()
                .filter(s -> s.localization() == Localization.USA)
                .findFirst()
                .orElse(cards.get(0));
        return mapToDTO(mainSet, cards);
    }

    @Mapping(target = "available", constant = "true")
    @Mapping(target = "labelName", source = "name")
    @Mapping(target = "rarity", source = "rarity.code")
    YuGiOhCardTranslationDTO mapToDTO(OfficialSiteCard card);

    default Map<Localization, YuGiOhCardTranslationDTO> mapToTranslationDTO(List<OfficialSiteCard> cards) {
        var map = new EnumMap<Localization, YuGiOhCardTranslationDTO>(Localization.class);

        for (var card : cards) {
            map.put(card.localization(), mapToDTO(card));
        }
        return map;
    }

    @Mapping(target = "translations", source = "sets")
    YuGiOhSetDTO mapToSetDTO(OfficialSiteSet mainSet, List<OfficialSiteSet> sets);

    default YuGiOhSetDTO mapToSetDTO(List<OfficialSiteSet> sets) {
        if(CollectionUtils.isEmpty(sets)) {
            return null;
        }

        var mainSet = sets.stream()
                .filter(s -> s.localization() == Localization.USA)
                .findFirst()
                .orElse(sets.get(0));
        var value = mapToSetDTO(mainSet, sets);

        value.setOfficialSitePids(sets.stream()
                .map(s -> new OfficialSitePidDTO(s.pid(), s.localization()))
                .distinct()
                .toList());
        return value;
    }

    @Mapping(target = "available", constant = "true")
    YuGiOhSetTranslationDTO mapToSetDTO(OfficialSiteSet set);

    default Map<Localization, YuGiOhSetTranslationDTO> mapToSetTranslationDTO(List<OfficialSiteSet> sets) {
        var map = new EnumMap<Localization, YuGiOhSetTranslationDTO>(Localization.class);

        for (var set : sets) {
            map.put(set.localization(), mapToSetDTO(set));
        }
        return map;
    }

    @Mapping(target = "translations", source = "sets")
    YuGiOhSerieDTO mapToSerieDTO(OfficialSiteSet mainSet, List<OfficialSiteSet> sets);

    default YuGiOhSerieDTO mapToSerieDTO(List<OfficialSiteSet> sets) {
        if(CollectionUtils.isEmpty(sets)) {
            return null;
        }

        var mainSet = sets.stream()
                .filter(s -> s.localization() == Localization.USA)
                .findFirst()
                .orElse(sets.get(0));

        return mapToSerieDTO(mainSet, sets);
    }

    @Mapping(target = "name", source = "serie")
    YuGiOhSerieTranslationDTO mapToSerieDTO(OfficialSiteSet set);

    default Map<Localization, YuGiOhSerieTranslationDTO> mapToSerieTranslationDTO(List<OfficialSiteSet> sets) {
        var map = new EnumMap<Localization, YuGiOhSerieTranslationDTO>(Localization.class);

        for (var set : sets) {
            map.put(set.localization(), mapToSerieDTO(set));
        }
        return map;
    }

}
