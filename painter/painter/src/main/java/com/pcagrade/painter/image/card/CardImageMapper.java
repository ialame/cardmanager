package com.pcagrade.painter.image.card;

import com.pcagrade.painter.common.image.card.CardImageDTO;
import com.pcagrade.painter.common.image.card.PublicCardImageDTO;
import com.pcagrade.painter.publicdata.AbstractPublicImageMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class CardImageMapper extends AbstractPublicImageMapper {

    // De CardImage vers DTO simple
    @Mapping(target = "cardId", source = "cardId")
    @Mapping(target = "localization", source = "localization")
    @Mapping(target = "imageId", source = "id")
    @Mapping(target = "fichier", source = "fichier")
    abstract CardImageDTO mapToDTO(CardImage image);

    // De CardImage vers PublicDTO - utilise String
    @Mapping(target = "cardId", source = "cardId")
    @Mapping(target = "localization", expression = "java(\"fr\")")
    @Mapping(target = "url", expression = "java(buildPublicUrl(image.getFichier()))")
    abstract PublicCardImageDTO mapToPublicDTO(CardImage image);

    // De DTO simple vers CardImage
    @Mapping(target = "cardId", source = "cardId")
    @Mapping(target = "fichier", source = "fichier")
    @Mapping(target = "localization", source = "localization")
    // Ignorer toutes les autres propriétés de CardImage
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "setSourceId", ignore = true)
    @Mapping(target = "setId", ignore = true)
    @Mapping(target = "langue", ignore = true)
    @Mapping(target = "traits", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "infos", ignore = true)
    @Mapping(target = "downloadedAt", ignore = true)
    @Mapping(target = "tailleImg", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "src", ignore = true)
    abstract void updateFromDTO(@MappingTarget CardImage cardImage, CardImageDTO dto);
}
