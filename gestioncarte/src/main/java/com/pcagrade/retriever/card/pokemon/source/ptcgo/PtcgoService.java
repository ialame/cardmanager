package com.pcagrade.retriever.card.pokemon.source.ptcgo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.cache.IEvictable;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.feature.FeatureService;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetRepository;
import com.pcagrade.retriever.card.pokemon.translation.IPokemonCardTranslatorFactory;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.ITranslator;
import jakarta.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@CacheConfig(cacheNames={"ptcgo"})
public class PtcgoService implements IEvictable, IPokemonCardTranslatorFactory {

	private static final Logger LOGGER = LogManager.getLogger(PtcgoService.class);
	private static final Map<Localization, String> LANG_FOLDER_MAP = Map.of(
			Localization.USA, "en_US",
			Localization.FRANCE, "fr_FR",
			Localization.GERMANY, "de_DE",
			Localization.SPAIN, "es_ES",
			Localization.ITALY, "it_IT",
			Localization.PORTUGAL, "pt_BR",
			Localization.NETHERLANDS, "nl_NL",
			Localization.RUSSIA, "ru_RU");

	@Value("${ptcgo-data.repo.url}")
	private String ptcgoRepo;

	@Value("${ptcgo-data.repo.folder}")
	private String ptcgoFolder;

	@Autowired
	PtcgoSetRepository ptcgoSetRepository;

	@Autowired
	PokemonSetRepository pokemonSetRepository;

	@Autowired
	PtcgoSetMapper ptcgoSetMapper;

	@Autowired
	FeatureService featureService;

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private ObjectMapper mapper;

	private Git git;
	private Instant lastPull;

	@Transactional
	public List<PtcgoSetDTO> findBySetId(Ulid setId) {
		return ptcgoSetRepository.findAllBySetId(setId).stream()
				.map(ptcgoSetMapper::mapToDTO)
				.toList();
	}

	@Transactional
	public void setFilesForSet(Ulid setId, List<PtcgoSetDTO> ptcgoSets) {
		if (setId == null || CollectionUtils.isEmpty(ptcgoSets)) {
			return;
		}

		var set = pokemonSetRepository.findById(setId);
		var oldSets = ptcgoSetRepository.findAllBySetId(setId);

		for (var ptcgoSet : oldSets) {
			ptcgoSets.stream()
					.filter(s -> s.getId() == ptcgoSet.getId())
					.findAny()
					.ifPresentOrElse(s -> {
								ptcgoSet.setFileName(s.getFileName());
								ptcgoSetRepository.save(ptcgoSet);
							},
							() -> ptcgoSetRepository.delete(ptcgoSet));
		}
		set.ifPresent(pokemonSet -> ptcgoSets.stream().filter(s -> s.getId() == 0).forEach(s -> {
			var ptcgoSet = new PtcgoSet();

			ptcgoSet.setSet(pokemonSet);
			ptcgoSet.setFileName(s.getFileName());
			ptcgoSetRepository.save(ptcgoSet);
		}));
	}

	@Override
	public ITranslator<PokemonCardTranslationDTO> createTranslator(PokemonSetDTO set, PokemonCardDTO card) {
		return new PtcgoTranslator(set);
	}

	public List<PtcgoTranslation> getSetTranslation(PokemonSetDTO set, Localization localization) {
		pullFromRepo();

		return getFileName(set).stream()
				.flatMap(name -> parseJson(ptcgoFolder + '/' + LANG_FOLDER_MAP.get(localization) + '/' + name + ".json").stream())
				.toList();
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	private List<PtcgoTranslation> parseJson(String path) {
		var cache = getCache();

		if (cache != null) {
			var value = cache.get(path);

			if (value != null) {
				var list = (List<PtcgoTranslation>) value.get();

				if (list != null) {
					return list;
				} else {
					cache.evict(path);
				}
			}
		}
		try {
			var file = new File(path);
			List<PtcgoTranslation> list = file.exists() ? List.of(mapper.readValue(file, PtcgoTranslation[].class)) : Collections.emptyList();

			if (cache != null) {
				cache.put(path, list);
			}
			return list;
		} catch (IOException e) {
			throw new PtcgoException("Error while parsing ptcgo-data json", e);
		}
	}

	private synchronized void pullFromRepo() {
		if (git == null) {
			File gitDir = new File(ptcgoFolder + "/.git");

			if (gitDir.exists()) {
				try (Repository repo = new FileRepositoryBuilder().setGitDir(gitDir).setMustExist(false).build()) {

					git = repo.isBare() ? cloneRepo() : Git.wrap(repo);
					evictCache();
				} catch (IOException e) {
					throw new PtcgoException("Error while getting ptcgo-data git repository", e);
				}
			} else {
				git = cloneRepo();
				evictCache();
			}
		} else if (lastPull != null && lastPull.plus(1, ChronoUnit.HOURS).isAfter(Instant.now())) {
			return;
		}
		LOGGER.info("Pulling ptcgo-data ({})", ptcgoRepo);
		try {
			if (git.fetch().call().getTrackingRefUpdates().stream().anyMatch(t -> t.getResult() != Result.NO_CHANGE)) {
				git.pull().setStrategy(MergeStrategy.THEIRS).call();
			}
			lastPull = Instant.now();
			evictCache();
		} catch (GitAPIException e) {
			throw new PtcgoException("Error while pulling ptcgo-data git repository", e);
		}
	}

	private void evictCache() {
		var cache = getCache();

		if (cache != null) {
			cache.clear();
		}
	}

	private Cache getCache() {
		return cacheManager.getCache("ptcgo");
	}

	private Git cloneRepo() {
		File folder = new File(ptcgoFolder);

		LOGGER.info("Cloning ptcgo-data ({}) into {}", () -> ptcgoRepo, folder::getAbsolutePath);
		try {
			return Git.cloneRepository().setURI(ptcgoRepo).setDirectory(folder).call();
		} catch (GitAPIException e) {
			throw new PtcgoException("Error while cloning ptcgo-data git repository", e);
		}
	}

	private List<String> getFileName(PokemonSetDTO set) {
		var value = ptcgoSetRepository.findAllBySetId(set.getId()).stream()
				.map(PtcgoSet::getFileName)
				.toList();

		if (!value.isEmpty()) {
			return value;
		}

		var shortName = set.getShortName();

		return StringUtils.isNotBlank(shortName) ? List.of(shortName) : Collections.emptyList();
	}

	@Override
	public void evict() {
		git = null;
		lastPull = null;
	}

	private class PtcgoTranslator implements ITranslator<PokemonCardTranslationDTO> {

		private final PokemonSetDTO set;
		private final Map<Localization, List<PtcgoTranslation>> translations;

		private PtcgoTranslator(PokemonSetDTO set)  {
			this.set = set;
			translations = new EnumMap<>(Localization.class);
		}

		@Override
		public Optional<PokemonCardTranslationDTO> translate(PokemonCardTranslationDTO source, Localization to) {
			if (source == null || !LANG_FOLDER_MAP.containsKey(to) || getFileName(set).isEmpty()) {
				return Optional.empty();
			}

			var from = source.getLocalization();

			if (from == null) {
				return Optional.empty();
			}

			try {
				var targetPtcgo = getTranslationList(to);

				return getTranslationList(from).stream()
						.filter(t -> match(source, t))
						.flatMap(ptcgoSource -> targetPtcgo.stream()
								.filter(ptcgoTarget -> ptcgoTarget.getId().equalsIgnoreCase(ptcgoSource.getId()))
								.map(ptcgoTarget -> createTranslation(source, to, ptcgoSource, ptcgoTarget)))
						.findAny();
			} catch (PtcgoException e) {
				LOGGER.error("Fail to translate using ptcgo-data", e);
				return Optional.empty();
			}
		}

		private PokemonCardTranslationDTO createTranslation(PokemonCardTranslationDTO source, Localization to, PtcgoTranslation ptcgoSource, PtcgoTranslation ptcgoTarget) {
			var target = source.copy();

			target.setLocalization(to);
			target.setName(patchName(ptcgoSource.getName(), source.getName(), ptcgoTarget.getName()));
			target.setLabelName(target.getName());
			target.setTrainer("");
			return target;
		}

		private boolean match(PokemonCardTranslationDTO translation, PtcgoTranslation ptcgo) {
			String number = translation.getNumber();
			String ptcgoNumber = ptcgo.getNumber();
			var split = number.split("/")[0];

			if (number.equalsIgnoreCase(ptcgoNumber) || split.equalsIgnoreCase(ptcgoNumber) || number.equals(ptcgoNumber + "/" + set.getTotalNumber())) {
				return true;
			}

			try {
				var parsedPtcgoNumber = Integer.parseInt(ptcgoNumber);

				if (split.endsWith(String.format("%03d", parsedPtcgoNumber))) {
					return true;
				}
				return Integer.parseInt(split) == parsedPtcgoNumber;
			} catch (NumberFormatException e) {
				return false;
			}
		}


		private String patchName(String original, String revised, String source) {
			var name = PCAUtils.clean(StringUtils.replaceChars(source, '-', ' '));
			int index = StringUtils.isNotBlank(revised) ? original.indexOf(revised) : -1;

			if (index != -1) {
				String subString = PCAUtils.substringBetween(name, PCAUtils.clean(original.substring(0, index)), PCAUtils.clean(original.substring(index + revised.length())));

				if (StringUtils.isNotBlank(subString)) {
					name = subString;
				}
			}
			return name;
		}

		private synchronized List<PtcgoTranslation> getTranslationList(Localization localization) {
			return translations.computeIfAbsent(localization, k -> getSetTranslation(set, k));
		}

		@Override
		public String getName() {
			return "ptcgo-data";
		}
	}
}
