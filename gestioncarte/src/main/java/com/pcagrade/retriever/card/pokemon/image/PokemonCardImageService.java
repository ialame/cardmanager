package com.pcagrade.retriever.card.pokemon.image;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.painter.common.image.IImageService;
import com.pcagrade.painter.common.image.ImageDTO;
import com.pcagrade.painter.common.image.card.CardImageDTO;
import com.pcagrade.painter.common.image.card.ICardImageService;
import com.pcagrade.painter.common.image.legacy.ILegacyImageService;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardService;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetService;
import com.pcagrade.retriever.card.pokemon.source.limitless.LimitlessService;
import com.pcagrade.retriever.card.pokemon.source.pokemoncom.PokemoncomService;
import com.pcagrade.retriever.image.ExtractedImageDTO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

@Service
public class PokemonCardImageService {

    private static final Logger LOGGER = LogManager.getLogger(PokemonCardImageService.class);


    private static final Comparator<ExtractedImageDTO> IMAGE_COMPARATOR = Comparator.<ExtractedImageDTO, Boolean>comparing(i -> !i.hasSource(LimitlessService.NAME))
            .thenComparing(i -> !i.hasSource(PokemoncomService.NAME));
            //.thenComparing(i -> !i.hasSource(LegacyPokemonCardImageExtractor.NAME))
            //.thenComparing(i -> !i.hasSource("pokellector"));

//    @Autowired
//    private List<IPokemonCardImageExtractor> cardImageExtractors;
    @Autowired
    private List<IPokemonCardImageExtractor2> cardImageExtractors;
    //private List<PokemoncomService> cardImageExtractors;
    //private List<LimitlessService> cardImageExtractors;
    @Autowired
    private PokemonSetService pokemonSetService;
    @Autowired
    private PokemonCardService pokemonCardService;
    @Autowired
    private IImageService imageService;
    @Autowired
    private ILegacyImageService legacyImageService;
    @Autowired
    private ICardImageService cardImageService;

    public Page<ExtractedPokemonImagesDTO> extractImages(Ulid setId, Pageable pageable, Localization localization) {
        var setOpt = pokemonSetService.findSet(setId);

        if (setOpt.isEmpty()) {
            return Page.empty();
        }

        var set = setOpt.get();
        //var localization = PokemonSetHelper.getSetLocalization(set);
        var images = getCards(setId, pageable).map(c -> new ExtractedPokemonImagesDTO(c.getId(), getImages(c, localization)));

        LOGGER.info("Extracted {} images ({}) for {} cards in set {}",
                () -> images.stream().mapToInt(i -> i.images().size()).sum(),
                () -> FileUtils.byteCountToDisplaySize(images.stream().mapToInt(ExtractedPokemonImagesDTO::size).sum()),
                images::getSize,
                () -> setId);
        return images;
    }

    @Nonnull
    private List<ExtractedImageDTO> getImages(PokemonCardDTO card, Localization localization) {
        return cardImageExtractors.stream()
                .<ExtractedImageDTO>mapMulti((e, downstream) -> {
                    LOGGER.debug("Extracting images for card {} in {} using {}", card::getId, () -> localization, e::name);
                    e.getImages(card, localization).forEach(downstream);
                })
                .distinct()
                .sorted(IMAGE_COMPARATOR)
                .toList();
    }

    private Page<PokemonCardDTO> getCards(Ulid setId, Pageable pageable) {
        var allCards = pokemonCardService.getAllCardsInSet(setId);
        var size = allCards.size();
        var start = (int) pageable.getOffset();

        if (start >= size) {
            return Page.empty();
        }
        var end = Math.min(start + pageable.getPageSize(), size);

        return new PageImpl<>(allCards.subList(start, end), pageable, allCards.size());
    }

    public void setImage(Ulid cardId, ExtractedImageDTO image) {
        if (image.hasSource(CurrentPokemonCardImageExtractor.NAME)) {
            LOGGER.debug("Ignoring current image for card {}", cardId);
            return;
        }

        LOGGER.info("Setting image for card {}", cardId);

        try {
            ImageDTO imageDTO = null;
            if (image.hasSource(LegacyPokemonCardImageExtractor.NAME)) {
                imageDTO = legacyImageService.restoreImage("cards/pokemon/", image.url(), image.internal()).orElse(null);
            }
            if (imageDTO == null) {
                imageDTO = imageService.create("cards/pokemon/", image.url(), image.internal(), getRawImage(image));
            }
            cardImageService.saveCardImage(new CardImageDTO(cardId, image.localization(), imageDTO.id(), imageDTO.path()));
        } catch (Exception e) {
            LOGGER.error("Failed to set image for card {}", cardId, e);
        }
    }

    public ExtractedImageDTO reloadImage(ExtractedImageDTO image) {
        if (image.hasSource(CurrentPokemonCardImageExtractor.NAME, LegacyPokemonCardImageExtractor.NAME)) {
            return image;
        }

        try {
            return new ExtractedImageDTO(image.localization(), image.source(), image.url(), image.internal(), Base64.getEncoder().encodeToString(getRawImage(image)));
        } catch (Exception e) {
            LOGGER.error("Failed to reload image {}", image, e);
            return image;
        }
    }

    private byte[] getRawImage(ExtractedImageDTO image) {
        if (StringUtils.isNotBlank(image.base64Image())) {
            return Base64.getDecoder().decode(image.base64Image());
        }

       return cardImageExtractors.stream()
                .filter(e -> image.hasSource(e.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No image extractor found for source " + image.source()))
                .getRawImage(image);
    }
}
