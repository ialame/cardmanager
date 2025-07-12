package com.pcagrade.retriever.card.onepiece.source.official;

import com.pcagrade.mason.commons.FilterHelper;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.onepiece.OnePieceCardDTO;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSetDTO;
import com.pcagrade.retriever.card.onepiece.set.translation.OnePieceSetTranslationDTO;
import com.pcagrade.retriever.card.onepiece.translation.OnePieceCardTranslationDTO;
import com.pcagrade.retriever.card.promo.PromoCardDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Mapper(config = PCAMapperConfig.class)
public abstract class OnePieceOfficialSiteMapper {

    private static final Comparator<Pair<Localization, String>> DISTRIBUTION_COMPARATOR = Comparator.<Pair<Localization, String>, Localization>comparing(Pair::getLeft, Localization::compareTo)
            .thenComparing(Pair::getRight, StringUtils::compareIgnoreCase);

    public OnePieceSetDTO mapToSetDTO(List<OnePieceOfficialSiteSet> sets) {
        if (CollectionUtils.isEmpty(sets)) {
            return null;
        }

        return mapToDTO(sets.isEmpty() ? null :sets.get(0), sets);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serieId", ignore = true)
    @Mapping(target = "officialSiteIds", source = "sets")
    protected abstract OnePieceSetDTO mapToDTO(OnePieceOfficialSiteSet mainSet, List<OnePieceOfficialSiteSet> sets);

    @AfterMapping
    protected void afterMapping(@MappingTarget OnePieceSetDTO dto, OnePieceOfficialSiteSet mainSet, List<OnePieceOfficialSiteSet> sets) {
        sets.forEach(set -> dto.getTranslations().put(set.localization(), mapToTranslationDTO(set)));
    }

    protected abstract OnePieceSetTranslationDTO mapToTranslationDTO(OnePieceOfficialSiteSet set);

    public OnePieceCardDTO mapToCardDTO(List<OnePieceOfficialSiteCard> cards) {
        if (CollectionUtils.isEmpty(cards)) {
            return null;
        }

        return mapToDTO(cards.isEmpty() ? null :cards.get(0), cards);
    }

    protected abstract OnePieceCardDTO mapToDTO(OnePieceOfficialSiteCard mainCard, List<OnePieceOfficialSiteCard> cards);

    @AfterMapping
    protected void afterMapping(@MappingTarget OnePieceCardDTO dto, OnePieceOfficialSiteCard mainCard, List<OnePieceOfficialSiteCard> cards) {
        cards.forEach(card -> dto.getTranslations().put(card.localization(), mapToTranslationDTO(card)));

        var promos = new ArrayList<PromoCardDTO>();

        cards.stream()
                .<Pair<Localization, String>>mapMulti((card, downstream) -> card.distributions().forEach(d -> downstream.accept(Pair.of(card.localization(), d))))
                .filter(FilterHelper.distinctByComparator(DISTRIBUTION_COMPARATOR))
                .map(p -> new PromoCardDTO(p.getRight(), p.getLeft()))
                .forEach(promos::add);

        if (CollectionUtils.isNotEmpty(promos) && !promos.isEmpty()) {
            promos.get(0).setUsed(true);
            dto.setPromos(promos);
        } else {
            dto.setPromos(Collections.emptyList());
        }
    }

    @Mapping(target = "labelName", source = "name")
    @Mapping(target = "available", constant = "true")
    protected abstract OnePieceCardTranslationDTO mapToTranslationDTO(OnePieceOfficialSiteCard card);

    @AfterMapping
    protected void afterMapping(@MappingTarget OnePieceCardTranslationDTO dto, OnePieceOfficialSiteCard card) {
        if (card.parallel() == 1) {
            dto.setName(dto.getName() + " Parallel");
            dto.setLabelName(dto.getLabelName() + " Parallel");
        } else if (card.parallel() > 1) {
            dto.setName(dto.getName() + " Parallel <" + card.parallel() + ">");
            dto.setLabelName(dto.getLabelName() + " Parallel <" + card.parallel() + ">");
        }
    }

}
