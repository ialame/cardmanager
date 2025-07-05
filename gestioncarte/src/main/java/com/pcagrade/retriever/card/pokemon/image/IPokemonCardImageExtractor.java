package com.pcagrade.retriever.card.pokemon.image;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.image.ExtractedImageDTO;

import java.util.List;

public interface IPokemonCardImageExtractor {

    List<ExtractedImageDTO> getImages(PokemonCardDTO card, Localization localization);

    String name();

    byte[] getRawImage(ExtractedImageDTO image);
}
