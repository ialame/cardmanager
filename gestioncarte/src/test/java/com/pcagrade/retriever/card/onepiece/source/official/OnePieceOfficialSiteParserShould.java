package com.pcagrade.retriever.card.onepiece.source.official;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(OnePieceOfficialSiteTestConfig.class)
class OnePieceOfficialSiteParserShould {

    @Autowired
    OnePieceOfficialSiteParser onePieceOfficialSiteParser;

    @Test
    void getSets() {
        var sets = onePieceOfficialSiteParser.getSets();

        assertThat(sets).isNotEmpty()
                .allSatisfy(s -> {
                    assertThat(s.id()).isPositive();
                    assertThat(s.name()).isNotBlank().doesNotContain("【", "】", "ブースターパック", "スタートデッキ");
                });
    }

    @Test
    void getUsCards() {
        var cards = onePieceOfficialSiteParser.getCards(Localization.USA, 569102, false);

        assertThat(cards).isNotEmpty().hasSize(154)
                .allSatisfy(c -> {
                    assertThat(c.name()).isNotBlank();
                    assertThat(c.number()).isNotBlank();
                });
        assertThat(cards.get(1)).satisfies(c -> {
            assertThat(c.number()).isEqualTo("OP02-001");
            assertThat(c.name()).isEqualTo("Edward.Newgate");
            assertThat(c.parallel()).isOne();
        });
    }

    @Test
    void getJpCards() {
        var cards = onePieceOfficialSiteParser.getCards(Localization.JAPAN, 556102, false);

        assertThat(cards).isNotEmpty().hasSize(154)
                .allSatisfy(c -> {
                    assertThat(c.name()).isNotBlank();
                    assertThat(c.number()).isNotBlank();
                });
        assertThat(cards.get(1)).satisfies(c -> {
            assertThat(c.number()).isEqualTo("OP02-001");
            assertThat(c.name()).isEqualTo("Edward.Newgate");
            assertThat(c.parallel()).isOne();
        });
    }
}
