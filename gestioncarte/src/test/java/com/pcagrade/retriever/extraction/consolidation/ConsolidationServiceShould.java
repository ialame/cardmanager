package com.pcagrade.retriever.extraction.consolidation;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardTestProvider;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.retriever.extraction.consolidation.source.ConsolidationSource;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(ConsolidationServiceTestConfig.class)
class ConsolidationServiceShould {

    @Autowired
    private ConsolidationService consolidationService;

    @Test
    void consolidate_should_createProxy() {
        var proxy = consolidationService.consolidate(PokemonCardDTO.class, List.of(new ConsolidationSource<>(new PokemonCardDTO(), 1)));

        assertThat(proxy).isNotNull()
                .isInstanceOf(PokemonCardDTO.class);
    }

    @Test
    void consolidate_shouldNot_createProxy_with_emptyList() {
        var proxy = consolidationService.consolidate(PokemonCardDTO.class, Collections.emptyList());

        assertThat(proxy).isNull();
    }

    @Test
    void consolidate_PokemonCardTranslations() {
        var t1 = new PokemonCardTranslationDTO();

        t1.setName("Venusaur");
        t1.setNumber("001");
        t1.setLocalization(Localization.USA);
        var t2 = new PokemonCardTranslationDTO();

        t2.setName("Florizarre");
        t2.setNumber("002");
        t2.setLocalization(Localization.USA);

        var translation = consolidationService.consolidate(PokemonCardTranslationDTO.class, List.of(
                new ConsolidationSource<>(t1, 10),
                new ConsolidationSource<>(t2, 1)
        ));

        assertThat(translation).isNotNull();
        assertThat(translation.getName()).isEqualTo("Venusaur");
        assertThat(translation.getNumber()).isEqualTo("001");
        assertThat(translation.getLocalization()).isEqualTo(Localization.USA);
    }

    @Test
    void consolidate_PokemonCards() {
        var c1 = PokemonCardTestProvider.venusaur();

        c1.getTranslations().get(Localization.USA).setName("Venusaur test");

        var c2 = PokemonCardTestProvider.venusaur();

        c2.getTranslations().get(Localization.USA).setName("Venusaur test2");

        var card = consolidationService.consolidate(PokemonCardDTO.class, List.of(
                new ConsolidationSource<>(c1, 10),
                new ConsolidationSource<>(c2, 1)
        ));

        assertThat(card).isNotNull();
        assertThat(card.getTranslations().get(Localization.USA).getName()).isEqualTo("Venusaur test");
    }
}
