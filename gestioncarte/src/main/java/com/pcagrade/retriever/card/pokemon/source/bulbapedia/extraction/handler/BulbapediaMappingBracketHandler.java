package com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.handler;

import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.bracket.BracketService;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCard;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.trainer.BulbapediaTrainerService;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class BulbapediaMappingBracketHandler implements IBulbapediaMappingHandler {

    @Autowired
    private BulbapediaTrainerService bulbapediaTrainerService;
    @Autowired
    private BracketService bracketService;

    @Override
    public void handle(PokemonCardDTO dto, BulbapediaPokemonCard card, Localization localization) {
        var name = card.getName();
        var strBrackets = card.getBrackets();
        var translations = dto.getTranslations();

        if (CollectionUtils.isNotEmpty(strBrackets)) {
            dto.setBrackets(strBrackets.stream().map(b -> bracketService.findOrCreate(b, localization)).toList());
        }
        for (var bracket : card.getBrackets()) {
            if (bulbapediaTrainerService.isTrainer(name, bracket)) {
                dto.setBrackets(dto.getBrackets().stream()
                        .filter(b -> !StringUtils.equalsIgnoreCase(b.getName(), bracket))
                        .toList());
                translations.values().forEach(t -> t.setTrainer(bracket));
            }
        }
    }
}
