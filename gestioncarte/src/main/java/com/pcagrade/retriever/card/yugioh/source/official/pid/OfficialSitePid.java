package com.pcagrade.retriever.card.yugioh.source.official.pid;

import com.pcagrade.mason.localization.ILocalized;
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
@Table(name = "j_yugioh_official_site_pid", uniqueConstraints = { @UniqueConstraint(columnNames = { "pid", "localization" }) })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class OfficialSitePid implements ILocalized {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String pid;
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

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public Localization getLocalization() {
        return localization;
    }

    @Override
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
