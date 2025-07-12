package com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.handler;

import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCard;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.parser.IBulbapediaParser;
import com.pcagrade.retriever.card.pokemon.tag.PokemonCardTagService;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class BulbapediaMappingTeraTypeHandler implements IBulbapediaMappingHandler {

    @Autowired
    private PokemonCardTagService pokemonCardTagService;

    @Autowired
    private IBulbapediaParser bulbapediaParser;
    @Override
    public void handle(PokemonCardDTO dto, BulbapediaPokemonCard card, Localization localization) {
        var link = card.getLink();

        if (StringUtils.isBlank(link)) {
            return;
        }

        var teraType = bulbapediaParser.findTeraType(link);

        if (teraType != null) {
            pokemonCardTagService.getTeraType(teraType).ifPresent(t -> dto.getTags().add(t));
        }
    }
}
