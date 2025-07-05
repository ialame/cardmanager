package com.pcagrade.retriever.card.pokemon.source.pokemoncom;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.image.ExtractedPokemonImagesDTO;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PokemoncomService implements IPokemonCardImageExtractor2 {
    public static final String NAME = "pokemoncom";
    @Autowired
    private PokemoncomParser pokellectorParser;
    @Autowired
    private PokemonSetService pokemonSetService;
    @Autowired
    private PokemonSerieService pokemonSerieService;
    @Autowired
    private PokemonSerieRepository pokemonSerieRepository;
    @Autowired
    private PokemoncomParser pokemoncomParser;

    @Override
    @Transactional
    public List<ExtractedImageDTO> getImages(PokemonCardDTO card, Localization localization) {
        var cardTranslation = card.getTranslations().get(localization);

        if (cardTranslation == null || !cardTranslation.isAvailable()) {
            return Collections.emptyList();
        }

        var number = cardTranslation.getNumber();

        String cardNumber = number.split("/")[0];
        try {
            cardNumber = String.valueOf(Integer.parseInt(cardNumber));
        } catch (NumberFormatException e) {
            System.out.println("Error parsing card number: " + cardNumber);
        }

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

        var total = set.getPrintedTotal();
        var setNameEnglish = set.getTranslations().get(Localization.USA).getName();
        String setCodePokemonCom = mapSetCodeForPokemonCom(setNameEnglish);
        if (setCodePokemonCom.equals(""))
            setCodePokemonCom = set.getShortName();
        String langCode = getLangCode(localization);
        String imageUrl = String.format(
                "https://assets.pokemon.com/static-assets/content-assets/cms2%s/img/cards/web/%s/%s_%s_%s.png",
                langCode, setCodePokemonCom.toUpperCase(), setCodePokemonCom.toUpperCase(),
                localization.getCode().equals("us") ? "EN" : localization.getCode().toUpperCase(), cardNumber
        );

        ExtractedImageDTO imageDTO = new ExtractedImageDTO(
                localization,
                "pokemon.com",
                imageUrl,
                false,
                null
        );
        return List.of(imageDTO);
    }
//
//        return pokellectorParser.getImages(path, localization).entrySet().stream()
//                .filter(e -> StringUtils.equalsIgnoreCase(number, PokemonCardHelper.rebuildNumber(e.getKey(), total)))
//               .map(Map.Entry::getValue)
//               .toList();
//    }

    @Override
    public String name() {
        return "pokemoncom";
    }

    @Override
    public byte[] getRawImage(ExtractedImageDTO image) {
        return pokemoncomParser.getImage(image.url());
    }


    private String getLangCode(Localization localization) {
        switch (localization.getCode()) {
            case "fr": return "-fr-fr";
            case "us": return "";
            case "es": return "-es-es";
            case "it": return "-it-it";
            case "de": return "-de-de";
            default: return "";
        }
    }

    private String mapSetCodeForPokemonCom(String setNameEnglish) {
        switch (setNameEnglish) {
            case "Scarlet & Violet": return "sv01";
            case "Paldea Evolved": return "sv02";
            case "Obsidian Flames": return "sv03";
            case "151": return "sv3pt5";
            case "Paradox Rift": return "sv04";
            case "Temporal Forces": return "sv05";
            case "Twilight Masquerade": return "sv06";
            case "Shrouded Fable": return "sv6pt5";
            case "Stellar Crown": return "sv07";
            case "Surging Sparks": return "sv08";
            case "Prismatic Evolutions": return "sv8pt5";
            case "Friendly Adventures": return "sv09";
            case "Scarlet & Violet Promo Cards": return "svp";
            case "Arceus": return "pl4";
            case "Pokémon Rumble": return "ru1";
            case "Detective Pikachu": return "det";
            case "Crown Zenith": return "SWSH12PT5GG";
            default: return "";
        }
    }
}
