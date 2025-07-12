package com.pcagrade.retriever.card.pokemon.bracket;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import com.pcagrade.mason.localization.Localization;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface BracketRepository extends MasonRevisionRepository<Bracket, Ulid> {

	Bracket findFirstByNameIgnoreCaseOrderById(String name);

	List<Bracket> findAllByNameIgnoreCaseOrderById(String name);

	Optional<Bracket> findFirstByNameIgnoreCaseAndLocalizationAndPcaBracketFalseOrderById(String name, Localization localization);
	
	List<Bracket> findAllByOrderByName();

}
