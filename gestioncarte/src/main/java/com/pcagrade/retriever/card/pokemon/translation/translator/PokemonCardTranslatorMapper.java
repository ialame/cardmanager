package com.pcagrade.retriever.card.pokemon.translation.translator;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.localization.LocalizationMapper;
import org.mapstruct.Mapper;

@Mapper(config = PCAMapperConfig.class, uses = LocalizationMapper.class)
public interface PokemonCardTranslatorMapper {

    PokemonCardTranslatorDTO mapToDto(PokemonCardTranslator status);

}
