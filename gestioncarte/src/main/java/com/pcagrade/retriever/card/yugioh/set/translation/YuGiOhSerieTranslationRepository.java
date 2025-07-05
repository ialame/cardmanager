package com.pcagrade.retriever.card.yugioh.set.translation;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.serie.SerieTranslation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YuGiOhSerieTranslationRepository extends MasonRepository<SerieTranslation, Ulid> {

    @Query("from SerieTranslation t join fetch treat(t.translatable as YuGiOhSerie) where lower(t.name) = lower(?1) and t.localization = ?2")
    List<SerieTranslation> findAllByNameIgnoreCaseAndLocalization(String name, Localization localization);

}
