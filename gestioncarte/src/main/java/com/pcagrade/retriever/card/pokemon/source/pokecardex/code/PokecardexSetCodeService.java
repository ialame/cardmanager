package com.pcagrade.retriever.card.pokemon.source.pokecardex.code;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PokecardexSetCodeService {

	private static final Logger LOGGER = LogManager.getLogger(PokecardexSetCodeService.class);

    @Autowired
    private PokecardexSetCodeRepository pokecardexSetCodeRepository;

    @Autowired
    private PokemonSetRepository pokemonSetRepository;

    public String getCode(Ulid setId) {
        return pokecardexSetCodeRepository.findBySetId(setId)
                .map(PokecardexSetCode::getCode)
                .orElse("");
    }

    public void setCode(Ulid setId, String code) {
        if (setId == null || StringUtils.isBlank(code)) {
            return;
        }
        var opt = pokemonSetRepository.findById(setId);

        if (opt.isEmpty()) {
            LOGGER.error("Set with id {} not found", setId);
            return;
        }

        var set = opt.get();
        var pokecardexSetCode = pokecardexSetCodeRepository.findBySetId(setId)
                .orElseGet(PokecardexSetCode::new);

        pokecardexSetCode.setSet(set);
        pokecardexSetCode.setCode(code);
        pokecardexSetCodeRepository.save(pokecardexSetCode);
        LOGGER.info("Saving Pokecardex code {} for set {}", code, setId);
    }

    public Optional<Ulid> findByCode(String code) {
        return pokecardexSetCodeRepository.findFirstByCode(code)
                .map(c -> c.getSet().getId());

    }
}
