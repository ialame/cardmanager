package com.pcagrade.painter.image.card;

import com.pcagrade.painter.common.image.card.CardImageDTO;
import com.pcagrade.painter.common.image.card.PublicCardImageDTO;
import com.pcagrade.painter.publicdata.AbstractPublicImageMapper;
import com.pcagrade.painter.publicdata.BuildPublicUrl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class CardImageMapper extends AbstractPublicImageMapper {

    @Mapping(target = "imageId", source = "image.id")
    @Mapping(target = "fichier", source = "image.path")
    abstract CardImageDTO mapToDTO(CardImage image);

    @Mapping(target = "url", source = "image.path", qualifiedBy = BuildPublicUrl.class)
    abstract PublicCardImageDTO mapToPublicDTO(CardImage image);

    abstract void updateFromDTO(@MappingTarget CardImage cardImage, CardImageDTO dto);

}
