package com.pcagrade.painter.image.card;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import com.pcagrade.mason.localization.Localization;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardImageRepository extends MasonRevisionRepository<CardImage, Ulid> {

    List<CardImage> findAllByCardId(Ulid cardId);
    Optional<CardImage> findFirstByCardIdAndLocalization(Ulid cardId, Localization localization);

    void deleteAllByCardIdAndLocalization(Ulid cardId, Localization localization);
}
