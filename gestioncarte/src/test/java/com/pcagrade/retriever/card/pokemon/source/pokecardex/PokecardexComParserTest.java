package com.pcagrade.retriever.card.pokemon.source.pokecardex;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PokecardexComParserTest {

    @Autowired
    private PokecardexComParser pokecardexComParser;

    @Test
    void parse_should_parseOBF() {
        var map = pokecardexComParser.parse("OBF");

        assertThat(map).isNotEmpty().hasEntrySatisfying("77/197", v -> assertThat(v).isEqualToIgnoringCase("Ampibidou"));
    }
}
