package com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.handler;

import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCard;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.parser.IBulbapediaParser;
import com.pcagrade.retriever.card.pokemon.tag.PokemonCardTagService;
import com.pcagrade.retriever.card.tag.CardTagDTO;
import com.pcagrade.retriever.card.tag.translation.CardTagTranslationDTO;
import com.pcagrade.retriever.card.tag.type.CardTagType;
import com.pcagrade.mason.localization.Localization;
import jakarta.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Order(3)
public class BulbapediaMappingFormeHandler implements IBulbapediaMappingHandler {

    @Autowired
    private IBulbapediaParser bulbapediaParser;
    @Autowired
    private PokemonCardTagService pokemonCardTagService;

    @Override
    public void handle(PokemonCardDTO dto, BulbapediaPokemonCard card, Localization localization) {
        var forme = bulbapediaParser.findForme(card.getLink());
        var tags = new ArrayList<>(dto.getTags());

        if (StringUtils.isNotBlank(forme)) {
            tags.add(getOrCreateFormeTag(forme));
            dto.setBrackets(dto.getBrackets().stream()
                    .filter(b -> !StringUtils.equalsIgnoreCase(b.getName(), forme))
                    .toList());

            addFormToName(dto, Localization.JAPAN, forme);
            addFormToName(dto, Localization.USA, forme);
        } else {
            var formes = dto.getBrackets().stream()
                    .filter(b -> StringUtils.containsIgnoreCase(b.getName(), "form"))
                    .toList();

            if (CollectionUtils.isNotEmpty(formes)) {
                for (var formeTag : formes) {
                    tags.add(getOrCreateFormeTag(formeTag.getName()));
                }
                dto.setBrackets(dto.getBrackets().stream()
                        .filter(b -> !StringUtils.containsIgnoreCase(b.getName(), "form"))
                        .toList());
            }
        }
        dto.setTags(tags);
    }

    private static void addFormToName(PokemonCardDTO dto, Localization localization, String forme) {
        dto.getTranslations().computeIfPresent(localization, (l, t) -> {
            t.setName(t.getName() + " " + forme);
            t.setLabelName(t.getLabelName() + " " + forme);
            return t;
        });
    }

    @Nonnull
    private CardTagDTO getOrCreateFormeTag(String forme) {
        return pokemonCardTagService.getFormeTag(forme).orElseGet(() -> {
            var tag = new CardTagDTO();

            tag.setType(CardTagType.FORME);

            setFormeName(tag, Localization.USA, forme);
            setFormeName(tag, Localization.JAPAN, forme);
            return tag;
        });
    }

    private static void setFormeName(CardTagDTO tag, Localization localization, String name) {
        tag.getTranslations().computeIfAbsent(localization, l -> {
            var translation = new CardTagTranslationDTO();

            translation.setLocalization(l);
            return translation;
        }).setName(name);
    }
}
