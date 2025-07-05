package com.pcagrade.retriever.card.certification;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardCertificationRepository extends MasonRepository<CardCertification, Ulid> {

    List<CardCertification> findAllByCardId(Ulid id);
}
