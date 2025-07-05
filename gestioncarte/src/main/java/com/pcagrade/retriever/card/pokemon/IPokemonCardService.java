package com.pcagrade.retriever.card.pokemon;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.HistoryTreeDTO;
import com.pcagrade.mason.localization.Localization;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface IPokemonCardService {

	Stream<PokemonCardDTO> findCardsInSet(String name, String number, Localization localization, Ulid setId, boolean distribution);

	List<PokemonCardDTO> getAllCardsInSet(Ulid setId);

	void deleteCard(Ulid id);

	void deleteInSet(Ulid setId);

	void merge(PokemonCardDTO dto, List<Ulid> ids);

	Ulid save(PokemonCardDTO dto);

	Optional<PokemonCardDTO> getCardById(Ulid id);

	List<PokemonCardDTO> getCardChildrenById(Ulid id);

	HistoryTreeDTO<PokemonCardDTO> getCardHistoryById(Ulid id);

	List<PokemonCardDTO> findParentCard(PokemonCardDTO card);

	Stream<PokemonCardDTO> findParentCard(String name, String number, Localization localization, Ulid setId);

	List<PokemonCardDTO> findSavedCards(PokemonCardDTO card);

	List<PokemonCardDTO> findCardsWithBracket(Ulid bracketId);

	List<String> getAllTypes();

	void rebuildIdsPrim(Ulid setId);

	Optional<PokemonCardDTO> findCardWithPromo(Ulid promoId);
}
