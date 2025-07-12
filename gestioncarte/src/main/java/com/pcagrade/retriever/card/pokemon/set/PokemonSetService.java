package com.pcagrade.retriever.card.pokemon.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.UlidHelper;
import com.pcagrade.retriever.card.pokemon.serie.PokemonSerie;
import com.pcagrade.retriever.card.pokemon.serie.PokemonSerieRepository;
import com.pcagrade.retriever.card.pokemon.set.translation.PokemonSetTranslation;
import com.pcagrade.retriever.card.pokemon.set.translation.PokemonSetTranslationRepository;
import com.pcagrade.retriever.card.set.CardSetHelper;
import com.pcagrade.retriever.card.set.CardSetTranslation;
import com.pcagrade.retriever.card.set.extraction.status.CardSetExtractionStatusRepository;
import com.pcagrade.retriever.file.SharedFileService;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@CacheConfig(cacheNames = { "pokemonSets" })
public class PokemonSetService {

	@Autowired
	private PokemonSetRepository pokemonSetRepository;
	@Autowired
	private PokemonSetTranslationRepository pokemonSetTranslationRepository;
	@Autowired
	private PokemonSerieRepository pokemonSerieRepository;
	@Autowired
	private PokemonSetMapper pokemonSetMapper;
	@Autowired
	private CardSetExtractionStatusRepository cardSetExtractionStatusRepository;
	@Autowired
	private SharedFileService sharedFileService;
	@Autowired
	private RevisionMessageService revisionMessageService;

	@Cacheable
	public List<PokemonSetDTO> getSets() {
		return pokemonSetRepository.findAll().stream()
				.map(pokemonSetMapper::mapToDTO)
				.toList();
	}

	@Cacheable
	public Optional<PokemonSetDTO> findSet(@Nullable Ulid id) {
		if (id == null) {
			return Optional.empty();
		}
		return pokemonSetRepository.findById(id)
				.map(pokemonSetMapper::mapToDTO);
	}

	@CacheEvict(allEntries = true)
	@RevisionMessage("Sauvegarde de l''extension {0}")
	public Ulid save(PokemonSetDTO setDto) {
		final PokemonSet set = pokemonSetRepository.getOrCreate(setDto.getId(), PokemonSet::new);
		var translations = setDto.getTranslations();

		for (var it = translations.values().iterator(); it.hasNext();) {
			var translation = it.next();

			if (!translation.isAvailable()) {
				translation.setName("");
			} else if (StringUtils.isBlank(translation.getName())) {
				it.remove();
			}
		}
		pokemonSetMapper.update(set, setDto);
		set.getTranslations().forEach(translation -> {
			if (!translations.containsKey(translation.getLocalization())) {
				set.setTranslation(translation.getLocalization(), null);
				translation.setTranslatable(null);
				pokemonSetTranslationRepository.delete((PokemonSetTranslation) translation);
			}
		});

		pokemonSerieRepository.findByNullableId(setDto.getSerieId()).ifPresent(s -> {
			var oldSerie = set.getSerie();

			if (oldSerie != null) {
				oldSerie.getSets().removeIf(set1 -> UlidHelper.equals(s.getId(), set.getId()));
			}
			set.setSerie(s);
			s.getSets().add(set);
		});
		var oldParent = set.getParent();

		if (oldParent != null) {
			oldParent.getChildren().removeIf(set1 -> UlidHelper.equals(set1.getId(), set.getId()));
		}
		pokemonSetRepository.findByNullableId(setDto.getParentId()).ifPresentOrElse(
				s -> {
					set.setParent(s);
					s.getChildren().add(set);
				},
				() -> set.setParent(null)
		);

		propagateReleaseDate(set);

		var returnSet = pokemonSetRepository.save(set);

        return returnSet.getId();
	}

	private void propagateReleaseDate(PokemonSet set) {
		var translation = set.getTranslation(Localization.USA);
		var releaseDate  = translation != null ? translation.getReleaseDate() : null;

		if (releaseDate != null) {
			for (var translation2 : set.getTranslations()) {
				if (translation2.getReleaseDate() == null) {
					translation2.setReleaseDate(releaseDate);
				}
			}
		}
	}

	public List<Localization> getSetLocalizations(Ulid setId) {
		return pokemonSetRepository.findById(setId)
				.map(set -> set.getTranslations().stream()
						.filter(CardSetTranslation::isAvailable)
						.map(ILocalized::getLocalization)
						.sorted()
						.toList()
				).orElseGet(Collections::emptyList);
	}

	@CacheEvict(allEntries = true)
	@RevisionMessage("Suppression de l''extension {0}")
    public void deleteSet(Ulid id) {
		cardSetExtractionStatusRepository.deleteByTargetId(id);
		pokemonSetRepository.deleteById(id);
    }

    public int getIdPca(Ulid setId) {
		if (setId == null) {
			return 0;
		}
		var set = pokemonSetRepository.findById(setId).orElse(null);

		if (set == null) {
			return 0;
		}
		var idPca = set.getIdPca();

		if (idPca == null) {
			idPca = buildIdPca(set);
		}
		return idPca;
    }

	private int buildIdPca(PokemonSet set) {
		if (!(set.getSerie() instanceof PokemonSerie serie)) {
			return 0;
		}
		var idPca = CardSetHelper.buildIdPca(serie.getIdPca(), pokemonSetRepository::existsByIdPca);

		set.setIdPca(idPca);
		pokemonSetRepository.save(set);
		revisionMessageService.addMessage("Création de l''ID PCA pour l''extension {0}", set.getId());
		return idPca;
	}

	@CacheEvict(allEntries = true)
	@RevisionMessage("Fusion des extensions {0} et {1}")
	public void mergeSets(Ulid id1, Ulid id2) {
		pokemonSetRepository.mergeSets(id1, id2);
	}

    public void setImage(Ulid id, String image) {
		if (id == null || StringUtils.isBlank(image)) {
			return;
		}

		var set = pokemonSetRepository.findById(id).orElse(null);

		if (set == null) {
			return;
		}

		var shortName = set.getShortName();

		if (StringUtils.isBlank(shortName)) {
			return;
		}

		var blackPath = "/images/symbole/pokemon/noir/" + shortName + ".png";
		var redPath = "/images/symbole/pokemon/rouge/" + shortName + ".png";

		sharedFileService.saveBase64(blackPath, image);
		sharedFileService.convertToRed(blackPath, redPath);
	}

    public Optional<PokemonSetDTO> findByShortName(String shortName) {
		if (StringUtils.isBlank(shortName)) {
			return Optional.empty();
		}

		return pokemonSetRepository.findFirstByShortName(shortName)
				.map(pokemonSetMapper::mapToDTO);
    }
}
