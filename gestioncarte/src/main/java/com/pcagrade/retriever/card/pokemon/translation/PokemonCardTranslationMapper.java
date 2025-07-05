package com.pcagrade.retriever.card.pokemon.translation;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.CardTranslation;
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
public abstract class PokemonCardTranslationMapper extends AbstractTranslationMapper<PokemonCardTranslation, PokemonCardTranslationDTO> {

	protected PokemonCardTranslationMapper() {
		super(PokemonCardTranslation.class);
	}

	@Mapping(target = "trainer", defaultValue = "")
	public abstract PokemonCardTranslationDTO mapToDto(PokemonCardTranslation translation);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "source", ignore = true)
	@Mapping(target = "translatable", ignore = true)
	@Mapping(target = "originalName", defaultValue = "")
	@Mapping(target = "trainer", defaultValue = "")
	public abstract PokemonCardTranslation mapFromDto(PokemonCardTranslationDTO dto);

	@InheritConfiguration
	public abstract void update(@MappingTarget PokemonCardTranslation translation, PokemonCardTranslationDTO dto);

	public Map<Localization, PokemonCardTranslationDTO> translationListToDtoMap(List<CardTranslation> translations) {
		return translationListToDtoMap(translations, this::mapToDto);
	}

	 public List<CardTranslation> dtoMapToTranslationList(Map<Localization, PokemonCardTranslationDTO> map) {
		return new ArrayList<>(dtoMapToTranslationList(map, this::mapFromDto));
	}

}
