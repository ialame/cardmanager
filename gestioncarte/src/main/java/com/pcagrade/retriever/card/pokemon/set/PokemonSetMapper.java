package com.pcagrade.retriever.card.pokemon.set;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.pokemon.set.extraction.status.PokemonSetExtractionStatusMapper;
import com.pcagrade.retriever.card.pokemon.set.translation.PokemonSetTranslationMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class,
		collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
		uses = { PokemonSetTranslationMapper.class, PokemonSetExtractionStatusMapper.class })
public interface PokemonSetMapper {

	@Mapping(target = "serieId", source = "serie.id")
	@Mapping(target = "parentId", source = "parent.id")
	@Mapping(target = "printedTotal", source = "printedTotal")
	PokemonSetDTO mapToDTO(PokemonSet set);

	@InheritInverseConfiguration
	@Mapping(target = "cards", ignore = true)
	@Mapping(target = "serie", ignore = true)
	@Mapping(target = "parent", ignore = true)
	void update(@MappingTarget PokemonSet set, PokemonSetDTO dto);
}
