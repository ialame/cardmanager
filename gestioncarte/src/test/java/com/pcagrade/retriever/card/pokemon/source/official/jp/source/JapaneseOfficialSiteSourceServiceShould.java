package com.pcagrade.retriever.card.pokemon.source.official.jp.source;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.TestUlidProvider;
import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.retriever.card.pokemon.source.official.jp.JapaneseOfficialSiteTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(JapaneseOfficialSiteTestConfig.class)
class JapaneseOfficialSiteSourceServiceShould {

    @Autowired
    private JapaneseOfficialSiteSourceRepository japaneseOfficialSiteSourceRepository;
    @Autowired
    private JapaneseOfficialSiteSourceService japaneseOfficialSiteSourceService;

    @Test
    void getPg() {
        var pgs = japaneseOfficialSiteSourceService.getPgs(TestUlidProvider.ID_1);

        assertThat(pgs).isNotEmpty().contains("851");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidParams")
    void dont_setPgs_with_invalidValues(Ulid id) {
        Mockito.clearInvocations(japaneseOfficialSiteSourceRepository);

        japaneseOfficialSiteSourceService.setPgs(id, Collections.emptyList());

        Mockito.verify(japaneseOfficialSiteSourceRepository, Mockito.never()).save(Mockito.any(JapaneseOfficialSiteSource.class));
    }

    private static Stream<Arguments> provideInvalidParams() {
        return Stream.of(
                Arguments.of(new Object[]{null}),
                Arguments.of(PokemonSetTestProvider.XY_ID)
        );
    }
    
}
