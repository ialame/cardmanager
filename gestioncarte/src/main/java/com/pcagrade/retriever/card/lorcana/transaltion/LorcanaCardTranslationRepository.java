package com.pcagrade.retriever.card.lorcana.transaltion;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import com.pcagrade.mason.localization.Localization;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LorcanaCardTranslationRepository extends MasonRevisionRepository<LorcanaCardTranslation, Ulid> {

    List<LorcanaCardTranslation> findAllByLocalizationAndFullNumber(Localization localization, String fullNumber);

}
