package com.pcagrade.retriever.card.pokemon.image;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.painter.common.image.IImageService;
import com.pcagrade.painter.common.image.card.ICardImageService;
import com.pcagrade.painter.common.publicdata.PublicUrlService;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.image.ExtractedImageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CurrentPokemonCardImageExtractor implements IPokemonCardImageExtractor {

    public static final String NAME = "current";

    @Autowired
    private IImageService imageService;
    @Autowired
    private ICardImageService cardImageService;
    @Autowired
    private PublicUrlService publicUrlService;

    @Override
    public List<ExtractedImageDTO> getImages(PokemonCardDTO card, Localization localization) {
        return cardImageService.findAllByCardId(card.getId()).stream()
                .filter(i -> i.localization() == localization)
                .<ExtractedImageDTO>mapMulti((i, downstream) -> imageService.findById(i.imageId()).ifPresent(image -> downstream.accept(new ExtractedImageDTO(localization, NAME, publicUrlService.buildPublicUrl(image.path()), image.internal(), null))))
                .toList();
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public byte[] getRawImage(ExtractedImageDTO image) {
        throw new UnsupportedOperationException("Current image should not be extracted from the database.");
    }
}
