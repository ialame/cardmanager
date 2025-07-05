package com.pcagrade.retriever.voucher;

import com.pcagrade.retriever.card.set.CardSet;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "voucher")
public class Voucher extends AbstractUlidEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id")
    private CardSet set;

    public CardSet getSet() {
        return set;
    }

    public void setSet(CardSet set) {
        this.set = set;
    }
}
