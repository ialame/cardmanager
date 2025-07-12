package com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(ExpansionBulbapediaTestConfig.class)
class ExpansionBulbapediaServiceShould {

    @Autowired
    ExpansionBulbapediaRepository expansionBulbapediaRepository;

    @Autowired
    ExpansionBulbapediaService expansionBulbapediaService;

    @Test
    void getExpansions() {
        var expansions = expansionBulbapediaService.getExpansions();

        assertThat(expansions).hasSizeGreaterThanOrEqualTo(10);
    }

    @Test
    void findGroup_pokemonGOus() {
        var group = expansionBulbapediaService.findGroup("https://bulbapedia.bulbagarden.net/wiki/Pok%C3%A9mon_GO_(TCG)", "Pokémon GO", Localization.USA);

        assertThat(group)
                .hasSize(1)
                .allMatch(e -> e.getLocalization() == Localization.USA, "localization");
    }

    @Test
    void findGroup_pokemonGOjp() {
        var group = expansionBulbapediaService.findGroup("https://bulbapedia.bulbagarden.net/wiki/Pok%C3%A9mon_GO_(TCG)", "Pokémon GO", Localization.JAPAN);

        assertThat(group)
                .hasSize(1)
                .allMatch(e -> e.getLocalization() == Localization.JAPAN, "localization");
    }

    @Test
    void findGroup_xy() {
        var group = expansionBulbapediaService.findGroup("https://bulbapedia.bulbagarden.net/wiki/XY_(TCG)", "XY", Localization.USA);

        assertThat(group)
                .hasSize(1)
                .allMatch(e -> e.getLocalization() == Localization.USA, "localization");
    }

    @Test
    void setExpansions_with_empty_list() {
        Mockito.clearInvocations(expansionBulbapediaRepository);

        expansionBulbapediaService.setExpansions(PokemonSetTestProvider.COLLECTION_X_ID, Collections.emptyList());

        Mockito.verify(expansionBulbapediaRepository, Mockito.never()).save(Mockito.notNull());
        Mockito.verify(expansionBulbapediaRepository).delete(Mockito.any());
    }

    @Test
    void setExpansions() {
        Mockito.clearInvocations(expansionBulbapediaRepository);

        var dto = new ExpansionBulbapediaDTO();

        dto.setFirstNumber("test");
        dto.setLocalization(Localization.USA);
        dto.setName("test");
        dto.setTableName("test");
        dto.setUrl("test");
        dto.setSetId(PokemonSetTestProvider.COLLECTION_X_ID);
        expansionBulbapediaService.setExpansions(PokemonSetTestProvider.ASTRAL_RADIANCE_ID, List.of(dto));

        Mockito.verify(expansionBulbapediaRepository).save(Mockito.any());
        Mockito.verify(expansionBulbapediaRepository, Mockito.never()).delete(Mockito.notNull());
    }

}
