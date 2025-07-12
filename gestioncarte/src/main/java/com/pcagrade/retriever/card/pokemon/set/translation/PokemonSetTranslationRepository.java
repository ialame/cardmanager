package com.pcagrade.retriever.card.pokemon.set.translation;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import com.pcagrade.mason.localization.Localization;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PokemonSetTranslationRepository extends MasonRepository<PokemonSetTranslation, Ulid> {
    List<PokemonSetTranslation> findAllByNameAndLocalizationOrderByAvailableDesc(String name, Localization localization);

}
