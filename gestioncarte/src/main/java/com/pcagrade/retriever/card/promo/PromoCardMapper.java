package com.pcagrade.retriever.card.promo;

import com.pcagrade.retriever.PCAMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Mapper(config = PCAMapperConfig.class)
public interface PromoCardMapper {

	@Mapping(target = "versionId", source = "version.id")
	@Mapping(target = "eventId", source = "event.id")
	PromoCardDTO mapToDTO(PromoCard promo);
	
	List<PromoCardDTO> mapToDTO(Collection<PromoCard> promo);

	@Mapping(target = "card", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "event", ignore = true)
	void update(@MappingTarget PromoCard promo, PromoCardDTO dto);

	static void applyPromoUsed(Collection<PromoCardDTO> promos, Collection<PromoCard> promoUsed) {
		for (var promo : promoUsed) {
			for (var promoDTO : promos) {
				if (Objects.equals(promoDTO.getId(), promo.getId())) {
					promoDTO.setUsed(true);
					break;
				}
			}
		}
	}

}
