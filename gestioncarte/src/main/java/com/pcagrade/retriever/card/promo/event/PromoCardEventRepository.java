package com.pcagrade.retriever.card.promo.event;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Repository
public interface PromoCardEventRepository extends MasonRevisionRepository<PromoCardEvent, Ulid>, JpaSpecificationExecutor<PromoCardEvent> {

}
