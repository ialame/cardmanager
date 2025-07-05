package com.pcagrade.retriever.card.yugioh.extraction;

import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetDTO;
import com.pcagrade.retriever.card.yugioh.set.translation.YuGiOhSetTranslationDTO;
import com.pcagrade.retriever.card.yugioh.source.official.pid.OfficialSitePidDTO;
import com.pcagrade.retriever.card.yugioh.source.yugipedia.set.YugipediaSetDTO;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class YuGiOhCardSourceServiceTest {

    @Autowired
    private List<IYuGiOhCardSourceService> yuGiOhCardSourceServices;

    @ParameterizedTest
    @MethodSource("provide_services")
    void extractCards_should_returnValidCards(IYuGiOhCardSourceService service) {
        var ioc = new YuGiOhSetDTO();
        var us = new YuGiOhSetTranslationDTO();

        us.setCode("IOC");
        us.setName("Invasion of Chaos");
        us.setLocalization(Localization.USA);
        ioc.getTranslations().put(Localization.USA, us);
        ioc.getOfficialSitePids().add(new OfficialSitePidDTO("11103000", Localization.USA));
        ioc.getYugipediaSets().add(new YugipediaSetDTO("Set_Card_Lists:Invasion_of_Chaos_(TCG-EN)", Localization.USA));

        var cards = service.extractCards(ioc);

        assertThat(cards).isNotEmpty().allSatisfy(s -> assertThat(s.getValue()).isNotNull().satisfies(card -> assertThat(card.getTranslations()).allSatisfy((l, t) -> {
            assertThat(t.getLocalization()).isEqualTo(l);
            assertThat(t.getName()).isNotEmpty();
            assertThat(t.getLabelName()).isNotEmpty();
            assertThat(t.getNumber()).isNotEmpty().startsWith("IOC-");
        })));
    }

    private Stream<Arguments> provide_services() {
        return yuGiOhCardSourceServices.stream().map(Arguments::of);
    }
}
