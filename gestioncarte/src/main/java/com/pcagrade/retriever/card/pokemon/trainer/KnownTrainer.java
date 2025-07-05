package com.pcagrade.retriever.card.pokemon.trainer;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import com.pcagrade.mason.localization.Localization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Subselect;

@Entity
@Table(name = "j_known_trainer")

@Subselect("""
    select ct.id as id, ct.locale as locale, replace(ct.name, ' FA', '') as name, replace(ct.label_name, ' FA', '') as label_name, pct.trainer as trainer
    from card_translation ct
    join pokemon_card_translation pct on ct.id = pct.id and ct.discriminator = 'pok'
    where pct.trainer is not null and pct.trainer != ''
    """)
public class KnownTrainer extends AbstractUlidEntity {

    @Column(name = "locale")
    private Localization localization;
    @Column
    private String name;
    @Column(name = "label_name")
    private String labelName;
    @Column
    private String trainer;
    
    public KnownTrainer() {

    }
    
    public KnownTrainer(Ulid ulid, Localization localization, String name, String name1, String trainer) {
        this.setId(ulid);
        this.localization = localization;
        this.name = name;
        this.labelName = name1;
        this.trainer = trainer;
    }
    
    public Localization getLocalization() {
        return localization;
    }
    
    @Column
    public String getName() {
        return name;
    }
    
    public String getLabelName() {
        return labelName;
    }
    
    @Column
    public String getTrainer() {
        return trainer;
    }
}
