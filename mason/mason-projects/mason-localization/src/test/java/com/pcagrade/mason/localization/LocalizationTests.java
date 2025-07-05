package com.pcagrade.mason.localization;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class LocalizationTests {

    @ParameterizedTest
    @EnumSource(Localization.class)
    void localization_should_haveAGroup(Localization localization) {
        var code = localization.getCode();

        assertThat(Optional.ofNullable(Localization.Group.getByCode(code))).hasValueSatisfying(group -> {
            assertThat(group.getCode()).isEqualTo(code);
            assertThat(group.getLocalizations()).hasSize(1).contains(localization);
        });
    }
}
