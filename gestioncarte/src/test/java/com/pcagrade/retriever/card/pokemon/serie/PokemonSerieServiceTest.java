package com.pcagrade.retriever.card.pokemon.serie;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.params.provider.JsonSource;
import com.pcagrade.mason.ulid.UlidHelper;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PokemonSerieServiceTest {

    @Autowired
    private PokemonSerieService pokemonSerieService;

    @ParameterizedTest
    @JsonSource({"RET-197"})
    void save_should_saveCard(PokemonSerieDTO serie) {
        var id = pokemonSerieService.save(serie);
        var savedSerie = pokemonSerieService.findById(id);

        assertThat(savedSerie).hasValueSatisfying(c -> assertThat(c).usingRecursiveComparison()
                .withComparatorForType(UlidHelper::compare, Ulid.class)
                .ignoringFields("id")
                .isEqualTo(serie))
                .hasValueSatisfying(c -> assertThat(c.getId()).isNotNull().isEqualTo(id));
    }

}
