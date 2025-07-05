package com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon;

import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.promo.event.PromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.extraction.handler.ITraitPromoEventHandler;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTrait;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitDTO;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.regex.Pattern;

public class ParenthesisTraitPromoEventHandler implements ITraitPromoEventHandler, IPokemonPromoEventHandler {

    private static final Pattern PARENTHESIS_PATTERN = Pattern.compile("(\\(.*?\\))");

    @Override
    public void handleTrait(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull List<PromoCardEventTraitDTO> traits, @Nonnull EventNameHolder nameToParse) {
        if (StringUtils.isBlank(nameToParse)) {
            return;
        }

        var matcher = PARENTHESIS_PATTERN.matcher(nameToParse);

        if (!matcher.find()) {
            return;
        }

        var parenthesis = matcher.group(1);

        nameToParse.removeWord(parenthesis);
        traits.add(createParenthesis(parenthesis, dto.getEvent()));
    }

    @Nonnull
    private static PromoCardEventTraitDTO createParenthesis(String name, @Nonnull PromoCardEventDTO event) {
        var stamp = ITraitPromoEventHandler.createTrait(name, PromoCardEventTrait.PARENTHESIS, TradingCardGame.POKEMON, event.getTranslations().keySet());

        for (var translation : stamp.getTranslations().values()) {
            translation.setName("");
            translation.setLabelName("");
        }
        return stamp;
    }
}
