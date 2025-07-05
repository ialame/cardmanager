package com.pcagrade.retriever.card.tag;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import com.pcagrade.retriever.card.tag.type.CardTagType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface CardTagRepository extends MasonRevisionRepository<CardTag, Ulid> {

    @Query("select ct from CardTag ct left join fetch ct.translations ctt where ct.type = ?1 and lower(ctt.name) = lower(?2) and ctt.localization = 'us'")
    Optional<CardTag> findByTypeUsName(CardTagType type, String name);
}
