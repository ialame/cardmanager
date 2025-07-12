package com.pcagrade.retriever.card.promo.version;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PromoCardVersionServiceTest {

    @Autowired
    private PromoCardVersionService promoCardVersionService;

    @Test
    void findAll_shouldNot_beEmpty() {
        var all = promoCardVersionService.findAll();

        assertThat(all).isNotEmpty();
    }

    @Test
    void findById_should_haveValue() {
        var opt = promoCardVersionService.findById(PromoCardVersionTestProvider.FIRST_PLACE_ID);

        assertThat(opt).isNotEmpty();
    }

}
