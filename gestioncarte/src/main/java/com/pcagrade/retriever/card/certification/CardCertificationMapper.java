package com.pcagrade.retriever.card.certification;

import com.pcagrade.retriever.PCAMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = PCAMapperConfig.class)
public interface CardCertificationMapper {

    CardCertificationDTO mapToDTO(CardCertification certification);

    CardCertification mapFromDTO(CardCertificationDTO dto);

}
