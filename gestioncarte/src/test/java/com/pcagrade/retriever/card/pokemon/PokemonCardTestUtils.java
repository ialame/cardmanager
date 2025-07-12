package com.pcagrade.retriever.card.pokemon;

import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import org.assertj.core.api.Condition;

import java.util.regex.Pattern;

public class PokemonCardTestUtils {

    public static final Condition<PokemonCardTranslationDTO> WITHOUT_INVALID_FEATURES = new Condition<>(c -> !hasInvalidFeatures(c), "without invalid features");

    private PokemonCardTestUtils() {}

    private static boolean hasInvalidFeatures(PokemonCardTranslationDTO dto) {
        var pattern = Pattern.compile("\\{\\d}");

        return pattern.matcher(dto.getName()).find() || pattern.matcher(dto.getLabelName()).find();
    }
}
