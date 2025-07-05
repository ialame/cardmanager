package com.pcagrade.retriever.card.pokemon.source.official.path;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(OfficialSiteSetPathTestConfig.class)
class OfficialSiteSetPathServiceShould {

    @Autowired
    OfficialSiteSetPathRepository officialSiteSetPathRepository;

    @Autowired
    OfficialSiteSetPathService officialSiteSetPathService;

    @Test
    void getPaths() {
        var path = officialSiteSetPathService.getPaths(PokemonSetTestProvider.ASTRAL_RADIANCE_ID);

        assertThat(path).hasSize(2).containsExactly("ss-series/swsh10", "ss-series/swsh10tg");
    }

    @SuppressWarnings("unchecked")
    @Test
    void setPaths() {
        Mockito.clearInvocations(officialSiteSetPathRepository);

        officialSiteSetPathService.setPaths(PokemonSetTestProvider.ASTRAL_RADIANCE_ID, List.of("ss-series/swsh10", "ss-series/swsh10tga"));

        ArgumentCaptor<List<OfficialSiteSetPath>> deleteCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<OfficialSiteSetPath> saveCaptor = ArgumentCaptor.forClass(OfficialSiteSetPath.class);

        Mockito.verify(officialSiteSetPathRepository).deleteAll(deleteCaptor.capture());

        var deleted = deleteCaptor.getValue();

        assertThat(deleted).hasSize(1).allMatch(p -> p.getSet().getId().equals(PokemonSetTestProvider.ASTRAL_RADIANCE_ID) && p.getPath().equals("ss-series/swsh10tg"));

        Mockito.verify(officialSiteSetPathRepository, Mockito.times(1)).save(saveCaptor.capture());

        var saved = saveCaptor.getValue();

        assertThat(saved.getSet().getId()).isEqualTo(PokemonSetTestProvider.ASTRAL_RADIANCE_ID);
        assertThat(saved.getPath()).isEqualTo("ss-series/swsh10tga");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidParams")
    void dont_setPaths_with_invalidValues(Ulid id) {
        Mockito.clearInvocations(officialSiteSetPathRepository);

        officialSiteSetPathService.setPaths(id, Collections.emptyList());

        Mockito.verify(officialSiteSetPathRepository, Mockito.never()).save(Mockito.any(OfficialSiteSetPath.class));
    }

    private static Stream<Arguments> provideInvalidParams() {
        return Stream.of(
                Arguments.of(new Object[]{null}),
                Arguments.of(PokemonSetTestProvider.XY_ID)
        );
    }

}
