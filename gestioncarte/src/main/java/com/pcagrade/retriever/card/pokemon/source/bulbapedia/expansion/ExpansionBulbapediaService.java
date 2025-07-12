package com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetRepository;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@CacheConfig(cacheNames={ "expansionBulbapedia" })
public class ExpansionBulbapediaService {

	@Value("${bulbapedia.url}")
	private String bulbapediaUrl;

	@Autowired
	private PokemonSetRepository pokemonSetRepository;
	@Autowired
	ExpansionBulbapediaRepository expansionBulbapediaRepository;
	@Autowired
	ExpansionBulbapediaMapper expansionBulbapediaMapper;

	@Cacheable
	public List<ExpansionBulbapediaDTO> getExpansions() {
		return expansionBulbapediaMapper.mapToDto(expansionBulbapediaRepository.findAllByOrderByName());
	}

	@CacheEvict(value = { "pokemonSets", "expansionBulbapedia", "expansionBulbapediaBySetId", "expansionBulbapediaByParentId" }, allEntries = true)
	public void save(ExpansionBulbapediaDTO dto) {
		ExpansionBulbapedia expansionBulbapedia = expansionBulbapediaRepository.findById(dto.getId()).orElseGet(ExpansionBulbapedia::new);

		expansionBulbapediaMapper.update(expansionBulbapedia, dto);
		if (dto.getSetId() != null) {
			pokemonSetRepository.findById(dto.getSetId()).ifPresent(expansionBulbapedia::setSet);
		}
		expansionBulbapediaRepository.save(expansionBulbapedia);
	}

	@Cacheable("expansionBulbapediaBySetId")
	public List<ExpansionBulbapediaDTO> findBySetId(Ulid setId) {
		return expansionBulbapediaRepository.findAllBySetId(setId).stream()
				.map(expansionBulbapediaMapper::mapToDto)
				.toList();
	}

	@Cacheable
	public Optional<ExpansionBulbapediaDTO> findById(int id) {
		return expansionBulbapediaRepository.findById(id)
				.map(expansionBulbapediaMapper::mapToDto);
	}

	@Cacheable
	public List<ExpansionBulbapediaDTO> findByUrl(String url) {
		return expansionBulbapediaRepository.findAllByUrl(url).stream()
				.map(expansionBulbapediaMapper::mapToDto)
				.toList();
	}

	public List<ExpansionBulbapediaDTO> findGroup(String url, String name, Localization localization) {
		return expansionBulbapediaRepository.findAllByUrlAndPage2Name(url, name)
				.stream()
				.<ExpansionBulbapediaDTO>mapMulti((e, downstream) -> getGroup(e).forEach(downstream))
				.filter(e -> e.getLocalization().equals(localization))
				.toList();
	}

	private List<ExpansionBulbapediaDTO> getGroup(ExpansionBulbapedia eb) {
		return this.findBySetId(eb.getSet().getId());
	}

	@CacheEvict(value = { "pokemonSets", "expansionBulbapedia", "expansionBulbapediaBySetId", "expansionBulbapediaByParentId" }, allEntries = true)
    public void setExpansions(Ulid setId, List<ExpansionBulbapediaDTO> expansionsBulbapedia) {
		expansionBulbapediaRepository.findAllBySetId(setId).forEach(e -> {
			if (expansionsBulbapedia.stream().noneMatch(eb -> eb.getId() == e.getId())) {
				expansionBulbapediaRepository.delete(e);
			}
		});
		for (var expansionBulbapedia : expansionsBulbapedia) {
			String url = expansionBulbapedia.getUrl();

			if (StringUtils.isNoneBlank(url, expansionBulbapedia.getName(), expansionBulbapedia.getTableName(), expansionBulbapedia.getFirstNumber())) {
				expansionBulbapedia.setSetId(setId);

				if (!url.startsWith("http")) {
					if (url.startsWith("/")) {
						url = url.substring(1);
					}
					expansionBulbapedia.setUrl(bulbapediaUrl + '/' + url);
				}
				save(expansionBulbapedia);
			}
		}
    }
}
