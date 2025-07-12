package com.pcagrade.retriever.card.pokemon.trainer;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Repository
public interface KnownTrainerRepository extends JpaRepository<KnownTrainer, Ulid> {

    @Query("select kt from KnownTrainer kt where kt.localization = ?1 and lower(?2) like lower(concat(kt.name, ' ', kt.trainer))")
    List<KnownTrainer> findByLocalizationAndLikeNameIgnoreCase(Localization localization, String name);
}
