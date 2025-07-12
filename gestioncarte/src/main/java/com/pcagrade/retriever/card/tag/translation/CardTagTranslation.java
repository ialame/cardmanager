package com.pcagrade.retriever.card.tag.translation;

import com.pcagrade.retriever.card.tag.CardTag;
import com.pcagrade.retriever.localization.translation.AbstractTranslationEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "card_tag_translation")
@Audited
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CardTagTranslation extends AbstractTranslationEntity<CardTag> {

}
