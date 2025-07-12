package com.pcagrade.retriever.card.promo;

import com.pcagrade.retriever.TestUlidProvider;
import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.promo.version.PromoCardVersionTestConfig;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(PromoCardTestConfig.class)
class PromoCardServiceShould {

    @Autowired
    PromoCardRepository promoCardRepository;

    @Autowired
    PromoCardService promoCardService;

    @Test
    void getPromosWithoutEvent() {
        var list = promoCardService.getPromosWithoutEvent();

        assertThat(list).isNotEmpty();
    }

    @Test
    void findVersionId_should_findStaff() {
        var promo = new PromoCardDTO();

        promo.setName("Scarlet & Violet Prerelease staff promo");

        var id = promoCardService.findVersionId(promo, TradingCardGame.POKEMON);

        assertThat(id).isEqualTo(PromoCardVersionTestConfig.STAFF_ID);
    }

    @Test
    void setPromosForCard_adding_promo() {
        Mockito.clearInvocations(promoCardRepository);
        var promo = new PromoCardDTO();

        promo.setName("Name");
        promo.setLocalization(Localization.USA);

        promoCardService.setPromosForCard(TestUlidProvider.ID_2, List.of(promo));

        assertThat(getPromosPassedToSave()).hasSize(1)
                .allSatisfy(promoCard -> {
                    assertThat(promoCard.getName()).isEqualTo("Name");
                    assertThat(promoCard.getLocalization()).isEqualTo(Localization.USA);
                    assertThat(promoCard.getCard().getId()).isEqualTo(TestUlidProvider.ID_2);
                });
    }

    @Test
    void setPromosForCard_promo_without_changing_it() {
        Mockito.clearInvocations(promoCardRepository);

        var promo = new PromoCardDTO();

        promo.setLocalization(Localization.USA);
        promo.setName("Sword & Shield Prerelease staff promo");
        promo.setEventId(TestUlidProvider.ID_1);

        promoCardService.setPromosForCard(TestUlidProvider.ID_3, List.of(promo));

        assertThat(getPromosPassedToSave()).hasSize(1)
                .allSatisfy(promoCard -> {
                    assertThat(promoCard.getName()).isEqualTo("Sword & Shield Prerelease staff promo");
                    assertThat(promoCard.getLocalization()).isEqualTo(Localization.USA);
                    assertThat(promoCard.getCard().getId()).isEqualTo(TestUlidProvider.ID_3);
                    assertThat(promoCard.getEvent().getId()).isEqualTo(TestUlidProvider.ID_1);
                });
    }

    @Test
    void setPromosForCard_delete_promos() {
        Mockito.clearInvocations(promoCardRepository);

        promoCardService.setPromosForCard(TestUlidProvider.ID_3, Collections.emptyList());

        Mockito.verify(promoCardRepository, Mockito.atLeastOnce()).deleteAll(Mockito.any());
    }

    private List<PromoCard> getPromosPassedToSave() {
        ArgumentCaptor<PromoCard> captor = ArgumentCaptor.forClass(PromoCard.class);

        Mockito.verify(promoCardRepository, Mockito.atLeastOnce()).save(captor.capture());
        return captor.getAllValues();
    }

}
