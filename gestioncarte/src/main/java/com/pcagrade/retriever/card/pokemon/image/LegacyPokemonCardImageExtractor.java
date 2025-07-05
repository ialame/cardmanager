package com.pcagrade.retriever.card.pokemon.image;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.painter.common.image.legacy.ILegacyImageService;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardRepository;
import com.pcagrade.retriever.image.ExtractedImageDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class LegacyPokemonCardImageExtractor implements IPokemonCardImageExtractor {

    public static final String NAME = "legacy";

    @Autowired
    private ILegacyImageService legacyImageService;
    @Autowired
    private PokemonCardRepository pokemonCardRepository;

    @Override
    public List<ExtractedImageDTO> getImages(PokemonCardDTO card, Localization localization) {
        return pokemonCardRepository.findById(card.getId())
                .map(c -> {
                    var imageId = c.getImageId();

                    if (imageId == null || imageId == 0) {
                        return null;
                    }
                    try {
                        var url = legacyImageService.findById(String.valueOf(imageId));

                        if (StringUtils.isBlank(url)) {
                            return null;
                        }

                        return new ExtractedImageDTO(localization, NAME, url, false, null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).stream().toList();
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public byte[] getRawImage(ExtractedImageDTO image) {
        throw new UnsupportedOperationException("Legacy image should not be extracted from the database.");
    }
}
