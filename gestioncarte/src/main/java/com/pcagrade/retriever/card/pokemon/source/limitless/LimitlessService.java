package com.pcagrade.retriever.card.pokemon.source.limitless;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.image.IPokemonCardImageExtractor;
import com.pcagrade.retriever.card.pokemon.image.IPokemonCardImageExtractor2;
import com.pcagrade.retriever.card.pokemon.serie.PokemonSerie;
import com.pcagrade.retriever.card.pokemon.serie.PokemonSerieRepository;
import com.pcagrade.retriever.card.pokemon.serie.PokemonSerieService;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetService;
import com.pcagrade.retriever.image.ExtractedImageDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class LimitlessService implements IPokemonCardImageExtractor2 {
    public static final String NAME = "limitless";
    @Autowired
    private LimitlessParser limitlessParser;
    @Autowired
    private PokemonSetService pokemonSetService;
    @Autowired
    private PokemonSerieService pokemonSerieService;
    @Autowired
    private PokemonSerieRepository pokemonSerieRepository;

    @Override
    @Transactional
    public List<ExtractedImageDTO> getImages(PokemonCardDTO card, Localization localization) {
        var cardTranslation = card.getTranslations().get(localization);

        if (cardTranslation == null || !cardTranslation.isAvailable()) {
            return Collections.emptyList();
        }

        var number = cardTranslation.getNumber();

        var opt = card.getSetIds().stream()
                .<PokemonSetDTO>mapMulti((setId, downstream) -> pokemonSetService.findSet(setId).ifPresent(downstream))
                .filter(s -> s.getTranslations().containsKey(localization))
                .findFirst();

        if (opt.isEmpty()) {
            return Collections.emptyList();
        }

        var set = opt.get();
        var translation = set.getTranslations().get(localization);

        if (translation == null) {
            return Collections.emptyList();
        }

        PokemonSerie serie = pokemonSerieRepository.findByNullableId(set.getSerieId()).orElse(null);

        var name = translation.getName();
        String serieName = serie.getTranslation(localization).getName();

        if (StringUtils.isBlank(name)) {
            return Collections.emptyList();
        }

        var path= limitlessParser.setNameToCode.get(set.getTranslations().get(Localization.USA).getName());
        var total = set.getPrintedTotal();

        return limitlessParser.getImages(path, localization).entrySet().stream()
                .filter(e ->
                        StringUtils.equalsIgnoreCase(number, PokemonCardHelper.rebuildNumber(e.getKey(), total))
                        || StringUtils.equalsIgnoreCase(number, PokemonCardHelper.rebuildNumber0(e.getKey(), total))
                )
                .map(Map.Entry::getValue)
                .toList();
    }

    @Override
    public String name() {
        return "limitless";
    }

    @Override
    public byte[] getRawImage(ExtractedImageDTO image) {
        return limitlessParser.getImage(image.url());
    }
}
