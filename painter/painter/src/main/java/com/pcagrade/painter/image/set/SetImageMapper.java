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
    @Mapping(target = "imageId", source = "image.id")
    abstract SetImageDTO mapToDTO(SetImage image);

    @Mapping(target = "url", source = "image.path", qualifiedBy = BuildPublicUrl.class)
    abstract PublicSetImageDTO mapToPublicDTO(SetImage image);

    abstract void updateFromDTO(@MappingTarget SetImage setImage, SetImageDTO dto);

}
