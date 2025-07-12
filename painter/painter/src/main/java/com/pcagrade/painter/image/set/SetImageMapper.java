package com.pcagrade.painter.image.set;

import com.pcagrade.painter.common.image.set.PublicSetImageDTO;
import com.pcagrade.painter.common.image.set.SetImageDTO;
import com.pcagrade.painter.publicdata.AbstractPublicImageMapper;
import com.pcagrade.painter.publicdata.BuildPublicUrl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class SetImageMapper extends AbstractPublicImageMapper {

    @Mapping(target = "setId", expression = "java(com.github.f4b6a3.ulid.Ulid.fast())")
    @Mapping(target = "localization", expression = "java(com.pcagrade.mason.localization.Localization.FRANCE)")
    @Mapping(target = "imageId", expression = "java(com.github.f4b6a3.ulid.Ulid.fast())")
    abstract SetImageDTO mapToDTO(SetImage image);

    @Mapping(target = "setId", expression = "java(com.github.f4b6a3.ulid.Ulid.fast())")
    @Mapping(target = "localization", expression = "java(com.pcagrade.mason.localization.Localization.FRANCE)")
    @Mapping(target = "url", expression = "java(\"http://localhost:8081/images/default.jpg\")")
    abstract PublicSetImageDTO mapToPublicDTO(SetImage image);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    abstract void updateFromDTO(@MappingTarget SetImage setImage, SetImageDTO dto);
}