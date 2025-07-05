package com.pcagrade.retriever.card.pokemon.source.pkmncards.path;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PkmncardsComSetPathService {

	private static final Logger LOGGER = LogManager.getLogger(PkmncardsComSetPathService.class);

    @Autowired
    private PkmncardsComSetPathRepository pkmncardsComSetPathRepository;

    @Autowired
    private PokemonSetRepository pokemonSetRepository;

    public String getPath(Ulid setId) {
        return pkmncardsComSetPathRepository.findBySetId(setId)
                .map(PkmncardsComSetPath::getPath)
                .orElse("");
    }

    public void setPath(Ulid setId, String path) {
    	if (setId == null || StringUtils.isBlank(path)) {
            return;
        }
        PkmncardsComSetPath pkmncardsComSetPath = pkmncardsComSetPathRepository.findBySetId(setId).orElseGet(PkmncardsComSetPath::new);

        pokemonSetRepository.findById(setId).ifPresent(pkmncardsComSetPath::setSet);
        pkmncardsComSetPath.setPath(path);
        pkmncardsComSetPathRepository.save(pkmncardsComSetPath);
        LOGGER.trace("Saving pkmncards.com site path {}  for set {}", path, setId);
    }

}
