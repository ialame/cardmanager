package com.pcagrade.retriever.card.promo.event;

import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTrait;

public class PromoCardHelper {

    private PromoCardHelper() { }

    public static String cleanPromoName(String name) {
        return PCAUtils.clean(name
                .replace("'", "")
                .replace("’", "")
                .replace("\"", "")
                .replace("stamped", PromoCardEventTrait.STAMP)
                .replace("holofoil", PromoCardEventTrait.HOLO));
    }

}
