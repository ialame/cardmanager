package com.pcagrade.retriever.card.onepiece.source.official.id;

import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.jpa.LocalizationAttributeConverter;
import com.pcagrade.mason.localization.jpa.LocalizationColumnDefinitions;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSet;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "j_one_piece_official_site_id")
public class OnePieceOfficialSiteId implements ILocalized {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "official_site_id")
    private int officialSiteId;

    @Column(columnDefinition = LocalizationColumnDefinitions.NON_NULL)
    @Convert(converter = LocalizationAttributeConverter.class)
    private Localization localization;

    @ManyToOne
    @JoinColumn(name = "set_id")
    private OnePieceSet set;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getOfficialSiteId() {
        return officialSiteId;
    }

    public void setOfficialSiteId(int officialSiteId) {
        this.officialSiteId = officialSiteId;
    }

    public OnePieceSet getSet() {
        return set;
    }

    public void setSet(OnePieceSet set) {
        this.set = set;
    }

    @Override
    public Localization getLocalization() {
        return localization;
    }

    @Override
    public void setLocalization(Localization localization) {
        this.localization = localization;
    }
}
