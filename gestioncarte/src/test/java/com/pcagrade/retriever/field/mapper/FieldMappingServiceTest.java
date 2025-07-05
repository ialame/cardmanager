package com.pcagrade.retriever.field.mapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class FieldMappingServiceTest {

    @Autowired
    private FieldMappingService fieldMappingService;

    @ParameterizedTest
    @MethodSource("provide_cardLists")
    void mapField_should_mapTest(String field, String source, String expected) {
        var result = fieldMappingService.map(field, source);

        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provide_cardLists() {
        return Stream.of(
                Arguments.of("test.1", "test", "foo"),
                Arguments.of("test.1", "tEst", "foo"),
                Arguments.of("test.2", "test", "foo"),
                Arguments.of("test.2", "tEst", "tEst"),
                Arguments.of("test.3", "test", "foo"),
                Arguments.of("test.3", "tEst", "tEst"),
                Arguments.of("cards.ygh.rarity", "Ultra Rare (Pharaoh's Rare)", "URPR"),
                Arguments.of("cards.ygh.rarity", "ultra Rare (Pharaoh's Rare)", "URPR"),
                Arguments.of("cards.ygh.rarity", "UR (PR)", "URPR"),
                Arguments.of("cards.ygh.rarity", "UR (RF)", "URPR")
        );
    }
}
