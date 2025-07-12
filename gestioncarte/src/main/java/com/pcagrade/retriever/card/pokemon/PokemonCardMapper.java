package com.pcagrade.retriever.card.pokemon;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.extraction.status.CardExtractionStatusMapper;
import com.pcagrade.retriever.card.pokemon.bracket.BracketMapper;
import com.pcagrade.retriever.card.pokemon.feature.Feature;
import com.pcagrade.retriever.card.pokemon.feature.FeatureMapper;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCard;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationMapper;
import com.pcagrade.retriever.card.promo.PromoCardDTO;
import com.pcagrade.retriever.card.promo.PromoCardMapper;
import com.pcagrade.retriever.card.set.CardSet;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Mapper(config = PCAMapperConfig.class,
		uses = { BracketMapper.class, FeatureMapper.class, PromoCardMapper.class, PokemonCardTranslationMapper.class, CardExtractionStatusMapper.class })
public abstract class PokemonCardMapper {

	@Autowired
	private PokemonFieldMappingService fieldMappingService;

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "brackets", ignore = true)
	@Mapping(target = "featureIds", ignore = true)
	@Mapping(target = "translations", ignore = true)
	@Mapping(target = "promos", ignore = true)
	@Mapping(target = "artist", ignore = true)
	public abstract PokemonCardDTO mapToDto(BulbapediaPokemonCard card, Localization localization);

    @BeforeMapping
    protected void beforeMapToDto(@MappingTarget PokemonCardDTO dto, BulbapediaPokemonCard card, Localization localization) {
    	mapToTranslationDtoMap(dto, card, localization);

		var promos = card.getPromos().stream()
				.map(s -> new PromoCardDTO(s, localization))
				.toList();

		if (CollectionUtils.isNotEmpty(promos)) {
			promos.get(0).setUsed(true);
			dto.setPromos(promos);
		} else {
			dto.setPromos(Collections.emptyList());
		}
    }

	protected void mapToTranslationDtoMap(PokemonCardDTO dto, BulbapediaPokemonCard card, Localization localization) {
		Map<Localization, PokemonCardTranslationDTO> map = new EnumMap<>(Localization.class);
		PokemonCardTranslationDTO translation = mapToTranslationDto(card, localization);

		if (localization == Localization.JAPAN && PokemonCardHelper.inUnnumbered(translation.getNumber(), "")) {
			translation.setNumber(PokemonCardHelper.NO_NUMBER);
		}
		map.put(localization, translation);
		dto.setTranslations(map);
	}

    @BeforeMapping
    protected void mapTranslation(@MappingTarget BulbapediaPokemonCard card, PokemonCardDTO dto, Localization localization) {
    	PokemonCardTranslationDTO translation = dto.getTranslations().get(localization);

    	if (translation != null) {
    		card.setName(translation.getName());
    		card.setNumber(translation.getNumber());
    	}
    }

	@Mapping(target = "brackets", source = "card.brackets")
	@Mapping(target = "name", ignore = true)
	@Mapping(target = "number", ignore = true)
	@Mapping(target = "promos", ignore = true)
	public abstract BulbapediaPokemonCard mapToBulbapediaCard(PokemonCardDTO card, Localization localization);

	@Mapping(target = "setIds", source = "cardSets")
	@Mapping(target = "featureIds", source = "features")
	@Mapping(target = "promos", source = "promoCards")
	@Mapping(target = "parentId", source = "parent.id")
	@Mapping(target = "artist", source = "artist.name")
	public abstract PokemonCardDTO mapToDto(PokemonCard card);

	@AfterMapping
	protected void mapToDtoAfterMapping(@MappingTarget PokemonCardDTO dto, PokemonCard card) {
		for (var translation : dto.getTranslations().values()) {
			var name = StringUtils.trimToEmpty(translation.getName());
			var labelName = StringUtils.trimToEmpty(translation.getLabelName());

			if (StringUtils.endsWith(name, PokemonCardHelper.FA_SUFFIX) || StringUtils.endsWith(labelName, PokemonCardHelper.FA_SUFFIX)) {
				translation.setName(StringUtils.trimToEmpty(StringUtils.removeEnd(name, PokemonCardHelper.FA_SUFFIX)));
				translation.setLabelName(StringUtils.trimToEmpty(StringUtils.removeEnd(labelName, PokemonCardHelper.FA_SUFFIX)));
				dto.setFullArt(true);
			}
		}

		PromoCardMapper.applyPromoUsed(dto.getPromos(), card.getPromoUsed());
	}

	@InheritInverseConfiguration
	@Mapping(target = "brackets", ignore = true)
	@Mapping(target = "cardSets", ignore = true)
	@Mapping(target = "features", ignore = true)
	public abstract void update(@MappingTarget PokemonCard card, PokemonCardDTO dto);

    @AfterMapping
    protected void updateAfterMapping(@MappingTarget PokemonCard card, PokemonCardDTO dto) {
		if (Boolean.TRUE.equals(card.isFullArt())) {
			for (var translation : card.getTranslations()) {
				translation.setName(translation.getName() + " FA");
				translation.setLabelName(translation.getLabelName() + " FA");
			}
		}
    }

	@Mapping(target = "labelName", source = "card.name")
	protected abstract PokemonCardTranslationDTO mapToTranslationDto(BulbapediaPokemonCard card, Localization localization);

	@AfterMapping
	protected void mapToTranslationDtoAfterMapping(@MappingTarget PokemonCardTranslationDTO dto, BulbapediaPokemonCard card, Localization localization) {
		dto.setRarity(fieldMappingService.mapRarity(localization, dto.getRarity()));
	}

	protected List<Ulid> getSetId(Set<CardSet> sets) {
		return sets.stream()
				.map(CardSet::getId)
				.filter(Objects::nonNull)
				.toList();
	}

	protected List<Ulid> getFeatureIds(Set<Feature> features) {
		return features.stream()
				.map(Feature::getId)
				.filter(Objects::nonNull)
				.toList();
	}
}
