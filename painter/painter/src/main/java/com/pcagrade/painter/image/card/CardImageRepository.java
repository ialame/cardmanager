package com.pcagrade.painter.image.card;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardImageRepository extends MasonRevisionRepository<CardImage, Ulid> {

    List<CardImage> findAllByCardId(Ulid cardId);

    // Le repository doit correspondre au type de la propriété dans l'entité
    // Puisque CardImage.localization est de type String, utilisez String ici
    Optional<CardImage> findFirstByCardIdAndLocalization(Ulid cardId, String localization);

    void deleteAllByCardIdAndLocalization(Ulid cardId, String localization);
}