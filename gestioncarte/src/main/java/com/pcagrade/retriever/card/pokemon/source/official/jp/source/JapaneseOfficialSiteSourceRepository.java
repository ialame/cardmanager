package com.pcagrade.retriever.card.pokemon.source.official.jp.source;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;

import java.util.List;

public interface JapaneseOfficialSiteSourceRepository extends MasonRepository<JapaneseOfficialSiteSource, Integer> {

    List<JapaneseOfficialSiteSource> findAllBySetId(Ulid setId);

}
