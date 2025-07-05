package com.pcagrade.retriever.card.pokemon.source.bulbapedia.trainer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface BulbapediaTrainerRepository extends JpaRepository<BulbapediaTrainer, Integer> {
    int countByNameIgnoreCaseAndTrainerIgnoreCase(String name, String trainer);
}
