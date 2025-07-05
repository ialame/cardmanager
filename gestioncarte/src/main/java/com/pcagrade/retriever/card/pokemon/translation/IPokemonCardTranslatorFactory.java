package com.pcagrade.retriever.card.pokemon.translation;

import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.localization.translation.ITranslator;

@FunctionalInterface
public interface IPokemonCardTranslatorFactory {

	ITranslator<PokemonCardTranslationDTO> createTranslator(PokemonSetDTO set, PokemonCardDTO card);



}
