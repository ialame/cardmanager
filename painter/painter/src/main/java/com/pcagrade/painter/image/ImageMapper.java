package com.pcagrade.painter.image;

import com.pcagrade.painter.common.image.ImageDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageDTO mapToDTO(Image image);
}
