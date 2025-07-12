package com.pcagrade.retriever.card.pokemon.source.pokecardex.code;

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

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(PokecardexComCodeTestConfig.class)
class PokecardexSetCodeServiceShould {

    @Autowired
    PokecardexSetCodeRepository pokecardexSetCodeRepository;

    @Autowired
    PokecardexSetCodeService pokecardexSetCodeService;

    @Test
    void getCode() {
        var code = pokecardexSetCodeService.getCode(PokemonSetTestProvider.XY_ID);

        assertThat(code).isEqualTo("XY");
    }

    @Test
    void setCode() {
        Mockito.clearInvocations(pokecardexSetCodeRepository);

        pokecardexSetCodeService.setCode(PokemonSetTestProvider.XY_ID, "XYA");

        ArgumentCaptor<PokecardexSetCode> saveCaptor = ArgumentCaptor.forClass(PokecardexSetCode.class);


        Mockito.verify(pokecardexSetCodeRepository, Mockito.times(1)).save(saveCaptor.capture());

        var saved = saveCaptor.getValue();

        assertThat(saved.getSet().getId()).isEqualTo(PokemonSetTestProvider.XY_ID);
        assertThat(saved.getCode()).isEqualTo("XYA");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidParams")
    void dont_setCode_with_invalidValues(Ulid id, String code) {
        Mockito.clearInvocations(pokecardexSetCodeRepository);

        pokecardexSetCodeService.setCode(id, code);

        Mockito.verify(pokecardexSetCodeRepository, Mockito.never()).save(Mockito.any(PokecardexSetCode.class));
    }

    private static Stream<Arguments> provideInvalidParams() {
        return Stream.of(
                Arguments.of(null, "XYA"),
                Arguments.of(PokemonSetTestProvider.XY_ID, null),
                Arguments.of(PokemonSetTestProvider.XY_ID, ""),
                Arguments.of(PokemonSetTestProvider.XY_ID, "  "),
                Arguments.of(PokemonSetTestProvider.LUCARIO_VSTAR_STARTER_SET_ID, "XYA")
        );
    }

}
