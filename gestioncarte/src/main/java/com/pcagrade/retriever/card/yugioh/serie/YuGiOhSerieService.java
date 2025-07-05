package com.pcagrade.retriever.card.yugioh.serie;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.yugioh.set.translation.YuGiOhSerieTranslationRepository;
import com.pcagrade.retriever.localization.translation.AbstractTranslationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "yugiohSerie")
public class YuGiOhSerieService {

    @Autowired
    private YuGiOhSerieRepository yuGiOhSerieRepository;
    @Autowired
    private YuGiOhSerieTranslationRepository serieTranslationRepository;
    @Autowired
    private YuGiOhSerieMapper yuGiOhSerieMapper;

    @Cacheable("serieByNameAndLocalisation")
    public Optional<YuGiOhSerieDTO> findSerieByNameAndLocalisation(String name, Localization localisation) {
        return serieTranslationRepository.findAllByNameIgnoreCaseAndLocalization(name, localisation).stream()
                .map(AbstractTranslationEntity::getTranslatable)
                .mapMulti(PCAUtils.cast(YuGiOhSerie.class))
                .map(yuGiOhSerieMapper::mapToDTO)
                .findFirst();
    }

    @Cacheable
    public List<YuGiOhSerieDTO> findAll() {
        return yuGiOhSerieRepository.findAll().stream()
                .map(yuGiOhSerieMapper::mapToDTO)
                .toList();
    }

    @Cacheable
    public Optional<YuGiOhSerieDTO> findById(Ulid id) {
        return yuGiOhSerieRepository.findById(id)
                .map(yuGiOhSerieMapper::mapToDTO);
    }

    @CacheEvict(value = {"yugiohSerie", "allYuGiOhOfficialSets", "filteredYuGiOhOfficialSets", "serieByNameAndLocalisation", "officialSiteSet"}, allEntries = true)
    @RevisionMessage("Sauvegarde de la serie {return}")
    public Ulid save(YuGiOhSerieDTO dto) {
        var serie = yuGiOhSerieRepository.findByNullableId(dto.getId())
                .orElseGet(YuGiOhSerie::new);

        yuGiOhSerieMapper.update(serie, dto);
        return yuGiOhSerieRepository.save(serie).getId();
    }

}
