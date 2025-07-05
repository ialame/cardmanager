package com.pcagrade.retriever.card.pokemon.translation;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import com.pcagrade.mason.localization.Localization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface PokemonCardTranslationRepository extends MasonRevisionRepository<PokemonCardTranslation, Ulid> {

	@Query("select t from PokemonCardTranslation t left join fetch treat(t.translatable as PokemonCard) c left join c.cardSets s where (t.name = ?1 or t.labelName = ?1) and t.number = ?2 and t.localization = ?3 and s.id = ?4")
	List<PokemonCardTranslation> findAllByNameAndNumberAndLocalizationAndSetId(String name, String fullNumber, Localization localization, Ulid setId);

	@Query("select t from PokemonCardTranslation t left join fetch treat(t.translatable as PokemonCard) c left join c.cardSets s where t.number = ?1 and t.localization = ?2 and s.id = ?3")
	List<PokemonCardTranslation> findAllByNumberAndLocalizationAndSetId(String number, Localization localization, Ulid setId);

}
