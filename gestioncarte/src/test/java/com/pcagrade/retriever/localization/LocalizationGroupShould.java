package com.pcagrade.mason.localization;

import jakarta.annotation.Nonnull;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collection;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
class LocalizationGroupShould {

    @Test
    void getUSAByCode() {
        var result = Localization.Group.getByCode("us");

        assetThatEnum(result).isEqualTo(Localization.Group.USA);
    }

    @Test
    void getJapanByCode() {
        var result = Localization.Group.getByCode("jp");

        assetThatEnum(result).isEqualTo(Localization.Group.JAPAN);
    }

    @Test
    void getWestByCode_from_empty() {
        var result = Localization.Group.getByCode("");

        assetThatEnum(result).isEqualTo(Localization.Group.WEST);
    }

    @Test
    void west_contains_USA_and_France() {
        assertThatCollection(Localization.Group.WEST).contains(Localization.USA, Localization.FRANCE);
    }

    @Test
    void japan_does_not_contains_USA_and_France() {
        assertThatCollection(Localization.Group.JAPAN).doesNotContain(Localization.USA, Localization.FRANCE);
    }

    @ParameterizedTest
    @MethodSource("allGroups")
    void none_is_empty(Localization.Group group) {
        assertThatCollection(group).isNotEmpty();
    }

    @Nonnull
    private AbstractCollectionAssert<?, Collection<? extends Localization>, Localization, ObjectAssert<Localization>> assertThatCollection(Collection<Localization> group) {
        return assertThat(group);
    }

    @Nonnull
    private ObjectAssert<Enum<Localization.Group>> assetThatEnum(Enum<Localization.Group> group) {
        return assertThat(group);
    }

    private static Stream<Arguments> allGroups() {
        return Stream.of(Localization.Group.values())
                .map(Arguments::of);
    }
}
