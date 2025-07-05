package com.pcagrade.retriever.card.promo.event.trait;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.promo.event.trait.translation.PromoCardEventTraitTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PromoCardEventTraitServiceTest {

    @Autowired
    private PromoCardEventTraitService promoCardEventTraitService;

    @Test
    void findAll_shouldNot_beEmpty() {
        var all = promoCardEventTraitService.findAll();

        assertThat(all).isNotEmpty();
    }

    @Test
    void findById_should_haveValue() {
        var opt = promoCardEventTraitService.findById(PromoCardEventTraitTestProvider.CRACKED_IDE_HOLO_ID);

        assertThat(opt).isNotEmpty();
    }

    @ParameterizedTest
    @MethodSource("provide_traitsToSave")
    void save_should_saveExisting(Ulid id, Consumer<PromoCardEventTraitDTO> consumer) {
        var trait = promoCardEventTraitService.findById(id).orElseThrow();

        consumer.accept(trait);

        var savedId = promoCardEventTraitService.save(trait);

        assertThat(savedId).isNotNull().isEqualTo(id);

        var saved = promoCardEventTraitService.findById(savedId);

        assertThat(saved).isNotEmpty().hasValueSatisfying(t -> {
            assertThat(t).usingComparator((t1, t2) -> t1 == t2 ? 0 : 1).isNotEqualTo(trait);
            assertThat(t).usingRecursiveComparison().isEqualTo(trait);
        });
    }

    private static Stream<Arguments> provide_traitsToSave() {
        return Stream.of(
                Arguments.of(PromoCardEventTraitTestProvider.CRACKED_IDE_HOLO_ID, (Consumer<PromoCardEventTraitDTO>) t -> t.setName("test")),
                Arguments.of(PromoCardEventTraitTestProvider.PIKACHU_PUMPKIN_ID, (Consumer<PromoCardEventTraitDTO>) t -> t.getTranslations().get(Localization.FRANCE).setName("")),
                Arguments.of(PromoCardEventTraitTestProvider.PIKACHU_PUMPKIN_ID, (Consumer<PromoCardEventTraitDTO>) t -> {
                    var spain = new PromoCardEventTraitTranslationDTO();

                    spain.setLocalization(Localization.SPAIN);
                    spain.setName("Trick or Trade");
                    spain.setLabelName("Trick or Trade");
                    t.getTranslations().put(Localization.SPAIN, spain);
                })
        );
    }
}
