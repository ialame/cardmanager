package com.pcagrade.retriever.card.pokemon.source.wiki.url;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import com.pcagrade.mason.localization.Localization;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WikiUrlRepository extends MasonRepository<WikiUrl, Integer> {
	
	List<WikiUrl> findAllBySetIdAndLocalization(Ulid setId, Localization localization);
}
