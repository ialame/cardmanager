package com.pcagrade.retriever.card.yugioh.source.yugipedia;

import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class YugipediaParserTest {

    @Autowired
    private YugipediaParser yugipediaParser;

    @ParameterizedTest
    @MethodSource("provide_cardLists")
    void getCards_should_returnValidCards_with(String url, Localization localization, int size, String prefix) {
        var cards = yugipediaParser.getCards(url, localization);

        assertThat(cards).isNotEmpty().hasSize(size).anySatisfy(c -> {
            assertThat(c.number()).isNotEmpty().startsWith(prefix);
            assertThat(c.name()).isNotEmpty();
            assertThat(c.rarity()).isNotEmpty();
        });
    }

    private static Stream<Arguments> provide_cardLists() {
        return Stream.of(
                Arguments.of("Set_Card_Lists:Invasion_of_Chaos_(TCG-EN)", Localization.USA, 112, "IOC-"),
                Arguments.of("Set_Card_Lists:Yu-Gi-Oh!_Duel_Monsters_International:_Worldwide_Edition_promotional_cards_(OCG-AE)", Localization.USA, 6, "GBI-"),
                Arguments.of("Set_Card_Lists:Magnificent_Mavens_(TCG-EN)", Localization.USA, 121, "MAMA-")
        );
    }

    @Test
    void getAllSets_should_returnValidSets() {
        var sets = yugipediaParser.getAllSets();

        assertThat(sets).isNotEmpty()
                .allSatisfy(s -> assertThat(s.translations()).isNotEmpty().allSatisfy((l, t) -> {
                    assertThat(t.localization()).isEqualTo(l);
                    assertThat(t.name()).isNotEmpty();
                    assertThat(t.prefix()).doesNotContain("-");
                }))
                .anySatisfy(s -> assertThat(s.name()).isEqualToIgnoringCase("King's Court"))
                .anySatisfy(s -> assertThat(s.name()).isEqualToIgnoringCase("Toon Chaos"));
    }

    @Test
    void getSetPages_should_containsKingCourt() {
        var pages = yugipediaParser.getSetPages("Template:Packs")
                .collectList()
                .block();

        assertThat(pages).isNotEmpty().anySatisfy(p -> assertThat(p).isEqualTo("King's Court"));
    }
}
