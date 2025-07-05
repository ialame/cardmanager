package com.pcagrade.retriever.card.onepiece.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.onepiece.source.official.id.OnePieceOfficialSiteId;
import com.pcagrade.retriever.card.set.CardSetTranslation;
import com.pcagrade.mason.localization.Localization;

public class OnePieceSetTestProvider {

    public static final Ulid OP_02_ID = Ulid.from("01GXGP2VAB7V5MPHP7CGA8VMZC");


    public static OnePieceSet op02() {
        var set = new OnePieceSet();

        set.setId(OP_02_ID);

        setTranslation(set, Localization.USA, "Paramount War");
        setTranslation(set, Localization.JAPAN, "Paramount War");

        addOfficialSiteId(set, Localization.USA, 569102);
        addOfficialSiteId(set, Localization.JAPAN, 556102);
        return set;
    }

    private static void setTranslation(OnePieceSet set, Localization localization, String name) {
        var us = new CardSetTranslation();

        us.setName(name);
        us.setLocalization(localization);
        set.setTranslation(localization, us);
    }

    private static void addOfficialSiteId(OnePieceSet set, Localization localization, int id) {
        var officialSiteId = new OnePieceOfficialSiteId();

        officialSiteId.setId(id);
        officialSiteId.setSet(set);
        officialSiteId.setOfficialSiteId(id);
        officialSiteId.setLocalization(localization);
        set.getOfficialSiteIds().add(officialSiteId);
    }

}
