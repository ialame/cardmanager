package com.pcagrade.retriever.card.tag;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.tag.type.CardTagType;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(CardTagTestConfig.class)
class CardTagServiceShould {

    @Autowired
    CardTagService cardTagService;

    @Test
    void findAll() {
        var tags = cardTagService.findAll();

        assertThat(tags).isNotEmpty();
    }

    @Test
    void findById() {
        var tag = cardTagService.findById(CardTagTestProvider.NORMAL_FORM_ID);

        assertThat(tag).isNotEmpty().hasValueSatisfying(t -> {
            assertThat(t.getId()).isEqualTo(CardTagTestProvider.NORMAL_FORM_ID);
            assertThat(t.getType()).isEqualTo(CardTagType.FORME);
            assertThat(t.getTranslations().get(Localization.USA).getName()).isEqualTo("Normal Forme");
        });
    }

}
