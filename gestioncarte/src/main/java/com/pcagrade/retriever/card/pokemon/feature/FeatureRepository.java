package com.pcagrade.retriever.card.pokemon.feature;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureRepository extends MasonRevisionRepository<Feature, Ulid> {

	List<Feature> findAllByOrderByPcaName();
}
