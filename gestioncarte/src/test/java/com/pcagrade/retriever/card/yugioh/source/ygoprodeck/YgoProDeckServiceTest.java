package com.pcagrade.retriever.card.yugioh.source.ygoprodeck;

import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetDTO;
import com.pcagrade.retriever.card.yugioh.set.translation.YuGiOhSetTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class YgoProDeckServiceTest {

    @Autowired
    private YgoProDeckService ygoProDeckService;

    @Test
    void extractCards_should_beEmpty_with_anniversaryEditions() {
        var lob = new YuGiOhSetDTO();
        var us = new YuGiOhSetTranslationDTO();

        us.setCode("LOB");
        us.setName("Legend Of Blue Eyes White Dragon 25th Anniversary Edition");
        us.setLocalization(Localization.USA);
        lob.getTranslations().put(Localization.USA, us);

        var cards = ygoProDeckService.extractCards(lob);

        assertThat(cards).isEmpty();;
    }
}
