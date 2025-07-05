package com.pcagrade.retriever.card.pokemon.serie;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@CacheConfig(cacheNames={ "pokemonSeries" })
public class PokemonSerieService {

	private static final Ulid UNNUMBERED_ID = Ulid.from("01G88M8GCFZB8PRER7EVZ30VMJ");

	@Autowired
	private PokemonSerieRepository pokemonSerieRepository;
	@Autowired
	private PokemonSerieMapper pokemonSerieMapper;

	@Cacheable
	public Optional<PokemonSerieDTO> findById(@Nullable Ulid id) {
		if (id == null) {
			return Optional.empty();
		}
		return pokemonSerieRepository.findById(id)
				.map(pokemonSerieMapper::mapToDTO);
	}

	@Cacheable
	public List<PokemonSerieDTO> getSeries() {
		return pokemonSerieRepository.findAll().stream()
				.sorted(Comparator.comparingInt(s -> {
					var idPca = s.getIdPca();

					return idPca != null ? idPca : Integer.MAX_VALUE;
				}))
				.map(pokemonSerieMapper::mapToDTO)
				.toList();
	}

	public Optional<PokemonSerieDTO> getUnnumberedSerie() {
		return findById(UNNUMBERED_ID);
	}

	@CacheEvict(allEntries = true)
	@RevisionMessage("Sauvegarde de la serie {0}")
	public Ulid save(PokemonSerieDTO dto) {
		var serie = pokemonSerieRepository.findByNullableId(dto.getId())
				.orElseGet(PokemonSerie::new);

		pokemonSerieMapper.update(serie, dto);
		return pokemonSerieRepository.save(serie).getId();
	}
}
