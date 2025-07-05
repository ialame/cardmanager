package com.pcagrade.retriever.card.yugioh;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringJUnitConfig
class YuGiOhCardHelperShould {

    @ParameterizedTest
    @MethodSource("provideCardNumbers")
    void extractCardNumber(String number, String expected) {
        assertThat(YuGiOhCardHelper.extractCardNumber(number)).isEqualTo(expected);
    }

    private static Stream<Arguments> provideCardNumbers() {
        return Stream.of(
                Arguments.of("POTD-SP003", "003"),
                Arguments.of("RP02-EN001", "001"),
                Arguments.of("HA02-SP001", "001"),
                Arguments.of("MP22-SP001", "001"),
                Arguments.of("MP22-FR022", "022"),
                Arguments.of("LDK2-SPJ01", "J01"),
                Arguments.of("LDK2-SPY26", "Y26")
        );
    }

}
