package com.pcagrade.retriever.card.pokemon.bracket.translation;

import com.pcagrade.retriever.card.pokemon.bracket.Bracket;
import com.pcagrade.retriever.localization.translation.AbstractTranslationEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "crochet_translation")
@AttributeOverride(name = "name", column = @Column(name = "nom"))
@Audited
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BracketTranslation extends AbstractTranslationEntity<Bracket> {

}
