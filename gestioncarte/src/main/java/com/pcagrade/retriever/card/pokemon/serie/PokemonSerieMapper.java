package com.pcagrade.retriever.card.pokemon.serie;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.pokemon.PokemonCardMapper;
import com.pcagrade.retriever.card.pokemon.serie.translation.PokemonSerieTranslationMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class,
		collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
		uses = { PokemonCardMapper.class, PokemonSerieTranslationMapper.class })
public interface PokemonSerieMapper {

	PokemonSerieDTO mapToDTO(PokemonSerie set);

	@InheritInverseConfiguration
	void update(@MappingTarget PokemonSerie set, PokemonSerieDTO dto);
}
