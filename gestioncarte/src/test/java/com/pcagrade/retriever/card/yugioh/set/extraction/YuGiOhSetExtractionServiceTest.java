package com.pcagrade.retriever.card.yugioh.set.extraction;

import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class YuGiOhSetExtractionServiceTest {

    @Autowired
    private YuGiOhSetExtractionService yuGiOhSetExtractionService;

    @BeforeAll
    void setUp() {
        yuGiOhSetExtractionService.clear();
        yuGiOhSetExtractionService.extract();
    }

    @Test
    void should_returnValidSets() {
        var sets = yuGiOhSetExtractionService.get();

        assertThat(sets).isNotEmpty().allSatisfy(s -> assertThat(s.getSet()).isNotNull().satisfies(set -> assertThat(set.getTranslations()).allSatisfy((l, t) -> {
            assertThat(t.getLocalization()).isEqualTo(l);
            assertThat(t.getName()).isNotEmpty();
        })));
    }

    @Test
    void shouldNot_containsMoreThan1IOC() {
        var ioc = yuGiOhSetExtractionService.get().stream()
                .filter(s -> {
                    var t = s.getSet().getTranslations();
                    return t.containsKey(Localization.USA) && StringUtils.equalsIgnoreCase(t.get(Localization.USA).getName(), "Invasion of Chaos");
                }).toList();

        assertThat(ioc).hasSize(1);
        assertThat(ioc.get(0).getSources()).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void shouldNot_containsMoreThan1IOC25Anniversary() {
        var ioc = yuGiOhSetExtractionService.get().stream()
                .filter(s -> {
                    var t = s.getSet().getTranslations();
                    return t.containsKey(Localization.USA) && StringUtils.equalsIgnoreCase(t.get(Localization.USA).getName(), "Invasion of Chaos (lc01 25th Anniversary Edition)");
                }).toList();

        assertThat(ioc).hasSize(1);
        assertThat(ioc.get(0).getSources()).hasSizeGreaterThanOrEqualTo(2);
    }


    // RET-240
    @Test
    void should_haveSDKWithDate() {
        var sdk = yuGiOhSetExtractionService.get().stream()
                .filter(s -> {
                    var t = s.getSet().getTranslations();
                    return t.containsKey(Localization.USA) && StringUtils.equalsIgnoreCase(t.get(Localization.USA).getName(), "Starter Deck: Kaiba");
                }).toList();

        assertThat(sdk).hasSize(1);
        assertThat(sdk.get(0).getSet()).satisfies(set -> assertThat(set.getTranslations()).allSatisfy((l, t) -> {
            assertThat(t.getLocalization()).isEqualTo(l);
            assertThat(t.getName()).isNotEmpty();
            assertThat(t.getReleaseDate()).isNotNull();
        }));
    }
}
