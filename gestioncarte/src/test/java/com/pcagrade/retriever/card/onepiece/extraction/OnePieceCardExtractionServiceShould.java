package com.pcagrade.retriever.card.onepiece.extraction;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSetTestProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(OnePieceCardExtractionTestConfig.class)
class OnePieceCardExtractionServiceShould {

    @Autowired
    OnePieceCardExtractionService onePieceCardExtractionService;

    @Test
    void extractCards() {
        var cards = onePieceCardExtractionService.extractCards(OnePieceSetTestProvider.OP_02_ID);

        assertThat(cards).isNotEmpty().hasSize(154);
        assertThat(cards.get(1)).isNotNull().satisfies(e -> assertThat(e.getCard()).isNotNull().satisfies(c -> {
            assertThat(c.getNumber()).isEqualTo("OP02-001");
            assertThat(c.getParallel()).isOne();
        }));
    }
}
