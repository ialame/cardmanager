package com.pcagrade.retriever.card.lorcana.source.mushu;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MushuParserTest {

    @Autowired
    MushuParser mushuParser;

    @ParameterizedTest
    @ValueSource(strings = {
            "1",
            "2",
            "3"
    })
    void getCards(String templateName) {
        var cards = mushuParser.getCards(templateName);

        assertThat(cards).isNotEmpty().hasSizeGreaterThanOrEqualTo(204).allSatisfy(c -> {
            assertThat(c.ink()).isNotBlank();
            assertThat(c.artist()).isNotBlank();
            assertThat(c.rarity()).isNotEmpty();
            assertThat(c.translations()).isNotEmpty().hasSizeGreaterThanOrEqualTo(3).allSatisfy((l, t) -> {
                assertThat(t.name()).isNotBlank();
                assertThat(t.number()).isNotBlank();
            });
        });
    }
}
