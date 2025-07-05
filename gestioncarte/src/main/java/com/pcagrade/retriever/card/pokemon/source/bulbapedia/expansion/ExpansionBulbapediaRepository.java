package com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion;

import com.github.f4b6a3.ulid.Ulid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ExpansionBulbapediaRepository extends JpaRepository<ExpansionBulbapedia, Integer> {

	List<ExpansionBulbapedia> findAllByOrderByName();

	List<ExpansionBulbapedia> findAllByUrlAndPage2Name(String url, String page2Name);

	List<ExpansionBulbapedia> findAllBySetId(Ulid setId);
	
	List<ExpansionBulbapedia> findAllByUrl(String url);
	
}
