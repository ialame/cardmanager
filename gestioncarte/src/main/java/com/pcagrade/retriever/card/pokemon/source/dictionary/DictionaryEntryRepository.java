package com.pcagrade.retriever.card.pokemon.source.dictionary;

import com.pcagrade.mason.jpa.repository.MasonRepository;
import com.pcagrade.mason.localization.Localization;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DictionaryEntryRepository extends MasonRepository<DictionaryEntry, Integer> {

	@Procedure("j_refresh_translation_dictionary")
	void refreshDictionary();

	Optional<DictionaryEntry> findFirstByFromAndToAndFromValueIgnoreCaseOrderByCountDesc(Localization from, Localization to, String fromValue);
}
