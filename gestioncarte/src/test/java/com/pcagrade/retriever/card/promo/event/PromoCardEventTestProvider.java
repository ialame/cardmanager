package com.pcagrade.retriever.card.promo.event;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.TestUlidProvider;

public class PromoCardEventTestProvider {

    public static final Ulid TRAINERS_TOOLKIT_2022_ID = Ulid.from("01H1KECN63TYP1Y3MAGZK8PJES");

    private PromoCardEventTestProvider() {}

    public static PromoCardEvent testEvent() {
        var event = new PromoCardEvent();

        event.setId(TestUlidProvider.ID_1);
        event.setName("Test");
        return event;
    }
}
