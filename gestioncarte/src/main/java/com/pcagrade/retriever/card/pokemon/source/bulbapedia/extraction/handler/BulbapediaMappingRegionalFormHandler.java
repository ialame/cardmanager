package com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.handler;

import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCard;
import com.pcagrade.retriever.card.pokemon.tag.PokemonCardTagService;
import com.pcagrade.retriever.card.pokemon.tag.RegionalForm;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class BulbapediaMappingRegionalFormHandler implements IBulbapediaMappingHandler {

    @Autowired
    private PokemonCardTagService pokemonCardTagService;

    @Override
    public void handle(PokemonCardDTO dto, BulbapediaPokemonCard card, Localization localization) {
        var name = card.getName();

        for (RegionalForm regionForm : RegionalForm.values()) {
            if (StringUtils.containsIgnoreCase(name, regionForm.getName())) {
                pokemonCardTagService.getRegionalFormTag(regionForm).ifPresent(t -> dto.getTags().add(t));
                break;
            }
        }
    }
}
