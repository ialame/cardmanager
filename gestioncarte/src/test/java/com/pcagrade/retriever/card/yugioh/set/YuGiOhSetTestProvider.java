package com.pcagrade.retriever.card.yugioh.set;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;

public class YuGiOhSetTestProvider {

    public static final Ulid AMAZING_DEFENDERS_ID = UlidCreator.getUlid();

    public static YuGiOhSet amazingDefenders() {
        var set = new YuGiOhSet();

        set.setId(AMAZING_DEFENDERS_ID);
        return set;
    }
}
