package com.pcagrade.mason.localization;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
class LocalizationShould {

    @Test
    void getUSAByCode() {
        var result = Localization.getByCode("us");

        assertThat(result).isEqualTo(Localization.USA);
    }

    @Test
    void getJapanByCode() {
        var result = Localization.getByCode("jp");

        assertThat(result).isEqualTo(Localization.JAPAN);
    }

    @Test
    void getNullByCode_from_empty() {
        var result = Localization.getByCode("");

        assertThat(result).isNull();
    }

    @Test
    void usa_dont_haveOriginalName() {
        var result = Localization.USA.hasOriginalName();

        assertThat(result).isFalse();
    }

    @Test
    void japan_hasOriginalName() {
        var result = Localization.JAPAN.hasOriginalName();

        assertThat(result).isTrue();
    }
}
