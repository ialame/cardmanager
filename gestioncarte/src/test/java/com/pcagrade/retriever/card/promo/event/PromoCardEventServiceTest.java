package com.pcagrade.retriever.card.promo.event;

import com.pcagrade.retriever.card.promo.event.translation.PromoCardEventTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PromoCardEventServiceTest {

    @Autowired
    private PromoCardEventService promoCardEventService;

    @Test
    void findAll_shouldNot_beEmpty() {
        var all = promoCardEventService.findAll();

        assertThat(all).isNotEmpty();
    }

    @Test
    void findById_should_haveValue() {
        var opt = promoCardEventService.findById(PromoCardEventTestProvider.TRAINERS_TOOLKIT_2022_ID);

        assertThat(opt).isNotEmpty();
    }

    @Test
    void save_should_saveNewEvent() {
        var event = new PromoCardEventDTO();
        var us = new PromoCardEventTranslationDTO();

        event.setName("Test");
        us.setName("Test");
        us.setLocalization(Localization.USA);
        us.setReleaseDate(LocalDate.now());
        event.getTranslations().put(Localization.USA, us);

        var savedId = promoCardEventService.save(event);

        assertThat(savedId).isNotNull();

        var saved = promoCardEventService.findById(savedId);

        assertThat(saved).hasValueSatisfying(e -> assertThat(e).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(event));
    }

}
