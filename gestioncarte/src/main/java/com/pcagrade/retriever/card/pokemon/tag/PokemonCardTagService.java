package com.pcagrade.retriever.card.pokemon.tag;

import com.pcagrade.retriever.card.tag.CardTagDTO;
import com.pcagrade.retriever.card.tag.CardTagService;
import com.pcagrade.retriever.card.tag.type.CardTagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PokemonCardTagService {

    @Autowired
    private CardTagService cardTagService;

    public Optional<CardTagDTO> getRegionalFormTag(RegionalForm region) {
        return cardTagService.findByTypeAndName(CardTagType.REGIONAL_FORM, region.getName());
    }

    public Optional<CardTagDTO> getTeraType(TeraType teraType) {
        return cardTagService.findByTypeAndName(CardTagType.TERA_TYPE, teraType.getName());
    }

    public Optional<CardTagDTO> getFormeTag(String forme) {
        return cardTagService.findByTypeAndName(CardTagType.FORME, forme);
    }
}
