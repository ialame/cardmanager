package com.pcagrade.retriever.card.yugioh.translation;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import com.pcagrade.mason.localization.Localization;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YuGiOhCardTranslationRepository extends MasonRevisionRepository<YuGiOhCardTranslation, Ulid> {

    List<YuGiOhCardTranslation> findAllByLocalizationAndNumber(Localization localization, String number);

}
