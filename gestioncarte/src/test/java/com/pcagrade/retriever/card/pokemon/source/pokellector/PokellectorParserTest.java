package com.pcagrade.retriever.card.pokemon.source.pokellector;

import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PokellectorParserTest {

    @Autowired
    PokellectorParser pokellectorParser;

    @Test
    void getUrl_should_returnUrl() {
        var map = pokellectorParser.getImages("Paldean-Fates-Expansion", Localization.USA);

        assertThat(map).isNotEmpty();
    }

}
