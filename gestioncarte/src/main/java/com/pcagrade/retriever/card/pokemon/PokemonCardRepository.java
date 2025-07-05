package com.pcagrade.retriever.card.pokemon;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.card.pokemon.set.PokemonSet;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface PokemonCardRepository extends MasonRevisionRepository<PokemonCard, Ulid> {

	@Query("select pc from PokemonCard pc join fetch pc.cardSets cs where cs.id = :setId")
	List<PokemonCard> findInSet(Ulid setId);

	@Query("select pc.type from PokemonCard pc group by pc.type")
	List<String> findAllTypes();

	List<PokemonCard> findAllByParentId(Ulid parentId);

	@Query("select c from PokemonCard c join fetch c.promoCards pc where pc.id = :promoId")
	Optional<PokemonCard> findByPromoId(Ulid promoId);


	//Optional<PokemonCard> findByNameAndCardSetsContaining(String name, PokemonSet set);

	@Query("SELECT p FROM PokemonCard p WHERE p.card = :name AND :set MEMBER OF p.cardSets")
	List<PokemonCard> findByNameAndCardSetsContaining(@Param("name") String name, @Param("set") PokemonSet set);

	@Query("SELECT DISTINCT p FROM PokemonCard p JOIN p.translations t " +
			"WHERE p.card = :name AND :set MEMBER OF p.cardSets " +
			"AND t.localization = :lang " +
			"AND (TREAT(t AS PokemonCardTranslation).number = :number OR " +
			"(TREAT(t AS PokemonCardTranslation).number IS NOT NULL AND " +
			"TRIM(LEADING '0' FROM CASE WHEN LOCATE('/', TREAT(t AS PokemonCardTranslation).number) > 0 " +
			"THEN SUBSTRING(TREAT(t AS PokemonCardTranslation).number, 1, LOCATE('/', TREAT(t AS PokemonCardTranslation).number) - 1) " +
			"ELSE TREAT(t AS PokemonCardTranslation).number END) = :number))")
	List<PokemonCard> findByNameAndNumberAndCardSetsContaining(
			@Param("name") String name,
			@Param("number") String number,
			@Param("set") PokemonSet set,
			@Param("lang") Localization lang);

	@Query("SELECT pc FROM PokemonCard pc " +
			"LEFT JOIN pc.translations t " +
			"WHERE pc.id = :setId " +
			"AND (:lang IS NULL OR t.localization = :lang) " +
			"AND (:lang IS NULL OR t.name = :cardName OR t.name IS NULL)")
	List<PokemonCard> findWithOptionalTranslation(
			@Param("setId") Ulid setId,
			@Param("lang") Localization lang,
			@Param("cardName") String cardName);


	@Query("SELECT DISTINCT pc FROM PokemonCard pc " +
			"JOIN pc.cardSets ps " +
			"JOIN pc.translations t " +
			"WHERE ps.id = :setId " +
			"AND t.localization = :lang " +
			"AND t.name = :cardName")
	List<PokemonCard> findBySetIdAndTranslationName(
			@Param("setId") Ulid setId,
			@Param("lang") Localization lang,
			@Param("cardName") String cardName);

	List<PokemonCard> findByNumberAndCardSetsContaining(String number, PokemonSet set);

}