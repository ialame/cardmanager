package com.pcagrade.retriever.card.pokemon.source.bulbapedia.parser;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCard;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCardPage2;
import com.pcagrade.retriever.card.pokemon.tag.TeraType;
import com.pcagrade.retriever.image.ExtractedImageDTO;
import com.pcagrade.retriever.image.IImageParser;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;

public interface IBulbapediaParser extends IImageParser {

    @Nonnull
    List<BulbapediaPokemonCard> parseExtensionPage(@Nullable String url, @Nullable String tableName, @Nullable String h3Name, @Nullable String firstNumber, boolean promo);

    boolean isInPage2(@Nullable String link, String number, @Nullable Localization localization, String setName, boolean unnumbered);

    @Nonnull
    String findOriginalName(@Nullable String url);

    @Nonnull
    String findForme(String url);
    @Nonnull
    String findArtistName(@Nullable String url);
    int findLevel(@Nullable String url);

    boolean isDeltaSpecies(@Nullable String url);

    @Nonnull
    List<BulbapediaPokemonCardPage2> findAssociatedCards(@Nonnull BulbapediaPokemonCard sourceCard, @Nonnull Localization localization, String setName, boolean unnumbered);

    @Nonnull
    List<ExtractedImageDTO> findImages(String url, Localization localization);


    @Nullable
    TeraType findTeraType(String url);
}
