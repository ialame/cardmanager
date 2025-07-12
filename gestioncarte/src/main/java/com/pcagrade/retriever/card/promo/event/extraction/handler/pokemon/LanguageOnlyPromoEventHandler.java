package com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon;

import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.translation.PromoCardEventTranslationDTO;
import com.pcagrade.mason.localization.Localization;

import jakarta.annotation.Nonnull;
import java.util.EnumMap;
import java.util.regex.Pattern;

public class LanguageOnlyPromoEventHandler implements IPokemonPromoEventHandler {

    private static final Pattern PATTERN = Pattern.compile("\\(printed in ([A-z]+) language only\\)", Pattern.CASE_INSENSITIVE);

    @Override
    public void handleEvent(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull EventNameHolder nameToParse) {
        var event = dto.getEvent();
        var matcher = PATTERN.matcher(event.getName());

        if (!matcher.find()) {
            return;
        }

        var localization = getLocalization(matcher.group(1));

        event.setName(PCAUtils.clean(matcher.replaceAll("")));
        nameToParse.remove(PATTERN);

        if (localization == null) {
            return;
        }

        var translation = event.getTranslations().get(localization);

        if (translation == null) {
            return;
        }
        translation.setName(nameToParse.toString());
        translation.setLabelName(nameToParse.toString());

        var map = new EnumMap<Localization, PromoCardEventTranslationDTO>(Localization.class);

        map.put(localization, translation);
        event.setTranslations(map);
    }

    private Localization getLocalization(String languageName) {
        return switch (languageName) {
            case "English" -> Localization.USA;
            case "French" -> Localization.FRANCE;
            case "German" -> Localization.GERMANY;
            case "Italian" -> Localization.ITALY;
            case "Portuguese" -> Localization.PORTUGAL;
            case "Spanish" -> Localization.SPAIN;
            default -> null;
        };
    }
}
