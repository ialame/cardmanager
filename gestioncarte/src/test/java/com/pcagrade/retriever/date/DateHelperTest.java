package com.pcagrade.retriever.date;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
class DateHelperTest {

    @ParameterizedTest
    @MethodSource("provide_dates")
    void parseDate_should_parseDates(String raw, LocalDate date) {
        var result = DateHelper.parseDate(raw);

        assertThat(result).isEqualTo(date);
    }

    private static Stream<Arguments> provide_dates() {
        return Stream.of(
                Arguments.of("", null),
                Arguments.of(" ", null),
                Arguments.of("November 2008", LocalDate.of(2008, 11, 1)),
                Arguments.of("November, 2008", LocalDate.of(2008, 11, 1)),
                Arguments.of(" November, 2008", LocalDate.of(2008, 11, 1)),
                Arguments.of("November 10, 2008", LocalDate.of(2008, 11, 10)),
                Arguments.of("2008", LocalDate.of(2008, 1, 1)),
                Arguments.of("10 November 2008", LocalDate.of(2008, 11, 10))
        );
    }
}
