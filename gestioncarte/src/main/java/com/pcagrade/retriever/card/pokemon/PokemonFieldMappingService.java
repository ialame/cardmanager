package com.pcagrade.retriever.card.pokemon;

import com.pcagrade.retriever.field.mapper.FieldMappingService;
import com.pcagrade.retriever.field.mapper.IResolvedMapping;
import com.pcagrade.mason.localization.Localization;
import org.springframework.stereotype.Service;

@Service
public class PokemonFieldMappingService {

    private final FieldMappingService fieldMappingService;

    public PokemonFieldMappingService(FieldMappingService fieldMappingService) {
        this.fieldMappingService = fieldMappingService;
    }

    public IResolvedMapping forRarity(Localization localization) {
        return fieldMappingService.forFields("cards.pok.rarity", "cards.pok." + localization.getCode() + ".rarity");
    }

    public String mapRarity(Localization localization, String source) {
        return forRarity(localization).map(source);
    }

}
