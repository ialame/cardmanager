package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.retriever.card.promo.event.PromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.translation.PromoCardEventTranslationDTO;
import com.pcagrade.mason.localization.Localization;

import java.util.List;

public interface IAddTranslationEventHandler extends IPromoEventHandler {

    default void initTranslations(PromoCardEventDTO event, List<Localization> localizations) {
        var translations = event.getTranslations();
        var name = event.getName();

        localizations.forEach(l -> translations.compute(l, (l2, t) -> {
            if (t != null) {
                t.setLocalization(l);
                t.setName(name);
                t.setLabelName(name);
                return t;
            }
            var translation = new PromoCardEventTranslationDTO();

            translation.setLocalization(l);
            translation.setName(name);
            translation.setLabelName(name);
            return translation;
        }));
    }
}
