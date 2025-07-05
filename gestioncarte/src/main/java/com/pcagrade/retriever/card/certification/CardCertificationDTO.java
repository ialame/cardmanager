package com.pcagrade.retriever.card.certification;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;

public class CardCertificationDTO implements ILocalized {

    private Ulid id;
    private Ulid cardId;
    private Localization localization;
    private String barcode;

    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }

    public Ulid getCardId() {
        return cardId;
    }

    public void setCardId(Ulid cardId) {
        this.cardId = cardId;
    }

    @Override
    public Localization getLocalization() {
        return localization;
    }

    @Override
    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
