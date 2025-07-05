package com.pcagrade.retriever.card.lorcana.source.mushu.set;

import com.pcagrade.retriever.card.lorcana.set.LorcanaSet;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "j_mushu_set")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MushuSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String key;
    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "set_id")
    private LorcanaSet set;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LorcanaSet getSet() {
        return set;
    }

    public void setSet(LorcanaSet set) {
        this.set = set;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
