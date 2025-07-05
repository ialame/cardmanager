package com.pcagrade.retriever.card.promo.event.extraction;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.promo.PromoCardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PromoCardEventExtractionServiceTest {

    private static final Ulid DARKNESS_ABLAZE_STAMPED_ID = Ulid.from("01H5W2EWJGMKT60WP8915VHECR");
    private static final Ulid PRIZE_FOR_OCTOBER_2023_MEETUP_EVENT_ID = Ulid.from("01HHPRSJBNWC4PAG115R2GBRYK");

    @Autowired
    PromoCardEventExtractionService promoCardEventExtractionService;
    @Autowired
    PromoCardService promoCardService;

    @Test
    void extract_should_returnValidEvents() {
        var events = promoCardEventExtractionService.extractEvents();

        assertThat(events).isNotEmpty().allSatisfy(e -> {
            assertThat(e.getId()).isNotNull();
            assertThat(e.getEvent()).isNotNull();
            assertThat(e.getPromos()).isNotEmpty();
        });
    }

    @Test
    void extract_should_returnValidEvent_with_OneId() {
        var events = promoCardEventExtractionService.extractEvents(List.of(DARKNESS_ABLAZE_STAMPED_ID));

        assertThat(events).isNotEmpty().hasSize(1).allSatisfy(e -> {
            assertThat(e.getId()).isNotNull();
            assertThat(e.getEvent()).isNotNull();
            assertThat(e.getPromos()).isNotEmpty();
        });
    }

    @Test
    void createExtractedEvent_should_beStamped() {
        var event = promoCardEventExtractionService.createExtractedEvent("Cosmos Holo Darkness Ablaze stamp gift with purchase exclusive (various countries)", List.of(promoCardService.findById(DARKNESS_ABLAZE_STAMPED_ID).orElseThrow()));

        assertThat(event.getEvent().getName()).isEqualTo("Cosmos Holo Darkness Ablaze stamp gift with purchase exclusive (various countries)");
        assertThat(event.getEvent().getTranslations()).hasSize(1).allSatisfy((l, t) -> {
            assertThat(t.getName()).isEqualTo("Stamped");
            assertThat(t.getLabelName()).isEqualTo("Stamped");
        });
    }

    @Test
    void createExtractedEvent_should_beMeetUpEvent() {
        var event = promoCardEventExtractionService.createExtractedEvent("for October 2023 Meet-up event", List.of(promoCardService.findById(PRIZE_FOR_OCTOBER_2023_MEETUP_EVENT_ID).orElseThrow()));

        assertThat(event.getEvent().getName()).isEqualTo("for October 2023 Meet-up event");
        assertThat(event.getEvent().getTranslations()).hasSize(1).allSatisfy((l, t) -> {
            assertThat(t.getName()).isEqualTo("Meet-up event");
            assertThat(t.getLabelName()).isEqualTo("Meet-up event");
        });
    }

}
