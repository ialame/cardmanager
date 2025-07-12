package com.pcagrade.retriever.card.pokemon.set.translation;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.set.CardSetTranslation;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.AbstractTranslationMapper;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(config = PCAMapperConfig.class)
public abstract class PokemonSetTranslationMapper extends AbstractTranslationMapper<PokemonSetTranslation, PokemonSetTranslationDTO> {

	protected PokemonSetTranslationMapper() {
		super(PokemonSetTranslation.class);
	}

	public abstract PokemonSetTranslationDTO mapTranslationToDTO(PokemonSetTranslation set);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "translatable", ignore = true)
	@Mapping(target = "labelName", source = "name")
	public abstract PokemonSetTranslation mapDTOToTranslation(PokemonSetTranslationDTO dto);

	@InheritConfiguration
	public abstract void update(@MappingTarget PokemonSetTranslation translation, PokemonSetTranslationDTO dto);

	public Map<Localization, PokemonSetTranslationDTO> translationListToDtoMap(List<CardSetTranslation> translations) {
		return translationListToDtoMap(translations, this::mapTranslationToDTO);
	}

	 public List<CardSetTranslation> dtoMapToTranslationList(Map<Localization, PokemonSetTranslationDTO> map) {
		return new ArrayList<>(dtoMapToTranslationList(map, this::mapDTOToTranslation));
	}

	public List<CardSetTranslation> update(@MappingTarget List<CardSetTranslation> translations, Map<Localization, PokemonSetTranslationDTO> map) {
		return this.update(translations.stream()
						.mapMulti(PCAUtils.cast(PokemonSetTranslation.class))
						.toList(), map, this::update, PokemonSetTranslation::new)
				.stream()
				.mapMulti(PCAUtils.cast(CardSetTranslation.class))
				.toList();
	}

}
