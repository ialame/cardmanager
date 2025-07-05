package com.pcagrade.retriever.card.yugioh.source.yugipedia.set;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.jpa.LocalizationAttributeConverter;
import com.pcagrade.mason.localization.jpa.LocalizationColumnDefinitions;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSet;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "j_yugipedia_set", uniqueConstraints = { @UniqueConstraint(columnNames = { "url", "localization" }) })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class YugipediaSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String url;
    @Column(columnDefinition = LocalizationColumnDefinitions.NON_NULL)
    @Convert(converter = LocalizationAttributeConverter.class)
    private Localization localization;

    @ManyToOne
    @JoinColumn(name = "set_id")
    private YuGiOhSet set;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Localization getLocalization() {
        return localization;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    public YuGiOhSet getSet() {
        return set;
    }

    public void setSet(YuGiOhSet set) {
        this.set = set;
    }
}
