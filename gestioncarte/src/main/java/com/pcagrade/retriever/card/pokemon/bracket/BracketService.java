package com.pcagrade.retriever.card.pokemon.bracket;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames={"bracket"})
public class BracketService {

	@Autowired
	private BracketRepository bracketRepository;

	@Autowired
	private BracketMapper bracketMapper;

	@Cacheable
	public List<BracketDTO> getBrackets() {
		return bracketRepository.findAllByOrderByName().stream()
				.map(bracketMapper::mapToDto)
				.toList();
	}

	@Cacheable("bracketByName")
	public BracketDTO findByName(String name) {
		if (StringUtils.isNotBlank(name)) {
			return bracketMapper.mapToDto(bracketRepository.findFirstByNameIgnoreCaseOrderById(name));
		}
		return null;
	}

	@Cacheable("allBracketsByName")
	public List<BracketDTO> findAllByName(String name) {
		if (StringUtils.isNotBlank(name)) {
			return bracketMapper.mapToDto(bracketRepository.findAllByNameIgnoreCaseOrderById(name));
		}
		return Collections.emptyList();
	}

	@Cacheable
	public Optional<BracketDTO> findById(Ulid id) {
		if (id == null) {
			return Optional.empty();
		}

		return bracketRepository.findById(id)
				.map(bracketMapper::mapToDto);
	}

	@CacheEvict(cacheNames = { "bracket", "bracketByName", "allBracketsByName" }, allEntries = true)
	public BracketDTO findOrCreate(String name, Localization localization) {
		return bracketRepository.findFirstByNameIgnoreCaseAndLocalizationAndPcaBracketFalseOrderById(name, localization)
				.map(bracketMapper::mapToDto)
				.orElseGet(() -> create(name, localization));
	}

	public BracketDTO create(String name, Localization localization) {
		var bracket = new BracketDTO();

		bracket.setName(name);
		bracket.setLocalization(localization);
		return bracket;
	}

	@CacheEvict(cacheNames = { "bracket", "bracketByName", "allBracketsByName" }, allEntries = true)
	@RevisionMessage("Sauvegarde du crochet {return}")
	public Ulid saveBracket(BracketDTO dto) {
		var bracket = bracketRepository.findByNullableId(dto.getId())
				.orElseGet(Bracket::new);

		bracketMapper.update(bracket, dto);
		return bracketRepository.save(bracket).getId();
	}

	@CacheEvict(cacheNames = { "bracket", "bracketByName", "allBracketsByName" }, allEntries = true)
	@RevisionMessage("Suppression du crochet {return}")
	public void deleteBracket(Ulid id) {
		bracketRepository.deleteById(id);
	}
}
