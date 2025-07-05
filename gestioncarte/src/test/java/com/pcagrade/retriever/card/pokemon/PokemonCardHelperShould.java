package com.pcagrade.retriever.card.pokemon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
class PokemonCardHelperShould {

    private static final Map<String, String> FIND_TRANSLATION_TEST_MAP = Map.of(
            "1/101", "101",
            "2", "102",
            "003", "103",
            "004/104", "104"
    );

    @Test
    void isNoNumber_return_false_with_null() {
        var result = PokemonCardHelper.isNoNumber(null);

        assertThat(result).isFalse();
    }

    @Test
    void isNoNumber_return_false_with_empty() {
        var result = PokemonCardHelper.isNoNumber("");

        assertThat(result).isFalse();
    }

    @Test
    void isNoNumber_return_true_with_none() {
        var result = PokemonCardHelper.isNoNumber("None");

        assertThat(result).isTrue();
    }

    @Test
    void isNoNumber_return_true_with_NoNumber() {
        var result = PokemonCardHelper.isNoNumber(PokemonCardHelper.NO_NUMBER);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"})
    void isNoNumber_return_false_with_anyString(String value) {
        var result = PokemonCardHelper.isNoNumber(value);

        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideUnnumberedNumbers")
    void to_be_unnumbered(String number, String totalNumber) {
        assertThat(PokemonCardHelper.inUnnumbered(number, totalNumber)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideNonUnnumberedNumbers")
    void to_not_be_unnumbered(String number, String totalNumber) {
        assertThat(PokemonCardHelper.inUnnumbered(number, totalNumber)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideAlternateNumbers")
    void to_be_alternate(String number) {
        assertThat(PokemonCardHelper.isAlternate(number)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideNonAlternateNumbers")
    void to_not_be_alternate(String number) {
        assertThat(PokemonCardHelper.isAlternate(number)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideAlternateToRemove")
    void removeAlternate(String number, String newNumber) {
        assertThat(PokemonCardHelper.removeAlternate(number)).isEqualTo(newNumber);
    }

    @ParameterizedTest
    @MethodSource("provideNumbersToFind")
    void findTranslation(String number, String newNumber) {
        assertThat(PokemonCardHelper.findTranslation(number, FIND_TRANSLATION_TEST_MAP)).hasValue(newNumber);
    }

    private static Stream<Arguments> provideUnnumberedNumbers() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of(PokemonCardHelper.NO_NUMBER, ""),
                Arguments.of("None", ""),
                Arguments.of("SM-P", "SM-P")
        );
    }

    @ParameterizedTest
    @MethodSource("provideTrainerNames")
    void extractTrainer(String raw, String name, String trainer) {
        var result = PokemonCardHelper.extractTrainer(raw);

        assertThat(result.getLeft()).isEqualTo(name);
        assertThat(result.getRight()).isEqualTo(trainer);
    }

    private static Stream<Arguments> provideNonUnnumberedNumbers() {
        return Stream.of(
                Arguments.of("188a/214", "214"),
                Arguments.of("182a/214", "214"),
                Arguments.of("182b/214", "214"),
                Arguments.of("058/060", "060"),
                Arguments.of("001/078", "078"),
                Arguments.of("001/071", "071"),
                Arguments.of("095/095", "095"),
                Arguments.of("DP05", "DP"),
                Arguments.of("SWSH05", "SWSH"),
                Arguments.of("001/SS-P", "SS-P")
        );
    }

    private static Stream<Arguments> provideAlternateNumbers() {
        return Stream.of(
                Arguments.of("188a/214"),
                Arguments.of("182a/214"),
                Arguments.of("182b/214"),
                Arguments.of("SM103a"),
                Arguments.of("SM103b")


        );
    }

    private static Stream<Arguments> provideNonAlternateNumbers() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("058/060"),
                Arguments.of("001/078"),
                Arguments.of("001/071"),
                Arguments.of("095/095"),
                Arguments.of("DP05"),
                Arguments.of("SWSH05"),
                Arguments.of("001/SS-P")
        );
    }

    private static Stream<Arguments> provideAlternateToRemove() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("188a", "188"),
                Arguments.of("182a", "182"),
                Arguments.of("182b", "182")
        );
    }

    private static Stream<Arguments> provideTrainerNames() {
        return Stream.of(
                Arguments.of("", "", ""),
                Arguments.of(null, "", ""),
                Arguments.of("test", "test", ""),
                Arguments.of("博士の研究（ナナカマド博士）", "博士の研究", "ナナカマド博士"),
                Arguments.of("test (test2)", "test", "test2"),
                Arguments.of("博士の研究", "博士の研究", "")
        );
    }

    private static Stream<Arguments> provideNumbersToFind() {
        return Stream.of(
                Arguments.of("001/101", "101"),
                Arguments.of("1/101", "101"),
                Arguments.of("002/102", "102"),
                Arguments.of("2/102", "102"),
                Arguments.of("002", "102"),
                Arguments.of("2", "102"),
                Arguments.of("003/103", "103"),
                Arguments.of("3/103", "103"),
                Arguments.of("003", "103"),
                Arguments.of("3", "103"),
                Arguments.of("004/104", "104"),
                Arguments.of("4/104", "104")
        );
    }

}
