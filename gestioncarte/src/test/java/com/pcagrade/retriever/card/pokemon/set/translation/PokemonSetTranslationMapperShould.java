package com.pcagrade.retriever.card.pokemon.set.translation;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.set.CardSetTranslation;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@RetrieverTest(PokemonSetTranslationTestConfig.class)
class PokemonSetTranslationMapperShould {

    @Autowired
    PokemonSetTranslationMapper pokemonSetTranslationMapper;

    @Test
    void update_with_removed_localization() {
        var us = new PokemonSetTranslation();
        var fr = new PokemonSetTranslation();

        us.setLocalization(Localization.USA);
        us.setName("XY");
        fr.setLocalization(Localization.FRANCE);
        fr.setName("XY");

        var list = List.<CardSetTranslation>of(us, fr);

        var usDTO = new PokemonSetTranslationDTO();

        usDTO.setLocalization(Localization.USA);
        usDTO.setName("XY");
        usDTO.setReleaseDate(LocalDate.now());

        var map = Map.of(Localization.USA, usDTO);


        var result = pokemonSetTranslationMapper.update(list, map);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLocalization()).isEqualTo(Localization.USA);
        assertThat(result.get(0).getReleaseDate().toLocalDate()).isEqualTo(LocalDate.now());
    }

}
