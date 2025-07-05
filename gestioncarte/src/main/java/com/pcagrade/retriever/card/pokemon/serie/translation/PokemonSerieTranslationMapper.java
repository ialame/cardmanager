package com.pcagrade.retriever.card.pokemon.serie.translation;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.AbstractTranslationMapper;
import com.pcagrade.retriever.serie.SerieTranslation;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Map;

@Mapper(config = PCAMapperConfig.class)
public abstract class PokemonSerieTranslationMapper extends AbstractTranslationMapper<PokemonSerieTranslation, PokemonSerieTranslationDTO> {

	protected PokemonSerieTranslationMapper() {
		super(PokemonSerieTranslation.class);
	}

	public abstract PokemonSerieTranslationDTO mapTranslationToDTO(PokemonSerieTranslation serie);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "translatable", ignore = true)
	public abstract PokemonSerieTranslation mapDTOToTranslation(PokemonSerieTranslationDTO dto);

	@InheritConfiguration
	public abstract void update(@MappingTarget PokemonSerieTranslation translation, PokemonSerieTranslationDTO dto);

	public Map<Localization, PokemonSerieTranslationDTO> translationListToDtoMap(List<SerieTranslation> translations) {
		return translationListToDtoMap(translations, this::mapTranslationToDTO);
	}

	public List<SerieTranslation> update(@MappingTarget List<SerieTranslation> translations, Map<Localization, PokemonSerieTranslationDTO> map) {
		return this.update(translations.stream()
						.mapMulti(PCAUtils.cast(PokemonSerieTranslation.class))
						.toList(), map, this::update, PokemonSerieTranslation::new)
				.stream()
				.mapMulti(PCAUtils.cast(SerieTranslation.class))
				.toList();
	}
	
}
