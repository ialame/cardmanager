package com.pcagrade.retriever.card.yugioh.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.retriever.card.set.CardSetHelper;
import com.pcagrade.retriever.card.yugioh.serie.YuGiOhSerie;
import com.pcagrade.retriever.card.yugioh.serie.YuGiOhSerieRepository;
import com.pcagrade.retriever.card.yugioh.source.official.pid.OfficialSitePidService;
import com.pcagrade.retriever.card.yugioh.source.yugipedia.set.YugipediaSetService;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class YuGiOhSetService {

    @Autowired
    private YuGiOhSerieRepository yuGiOhSerieRepository;
    @Autowired
    private YuGiOhSetRepository yuGiOhSetRepository;
    @Autowired
    private YuGiOhSetMapper yuGiOhSetMapper;
    @Autowired
    private OfficialSitePidService officialSitePidService;
    @Autowired
    private YugipediaSetService yugipediaSetService;
    @Autowired
    private RevisionMessageService revisionMessageService;

    public List<YuGiOhSetDTO> findAll() {
        return yuGiOhSetRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public Optional<YuGiOhSetDTO> findById(Ulid id) {
        return yuGiOhSetRepository.findById(id)
                .map(this::mapToDTO);
    }

    @Nonnull
    private YuGiOhSetDTO mapToDTO(YuGiOhSet s) {
        var dto = yuGiOhSetMapper.mapToDTO(s);

        dto.setOfficialSitePids(officialSitePidService.getPids(dto.getId()));
        dto.setYugipediaSets(yugipediaSetService.getYugipediaSets(dto.getId()));
        return dto;
    }

    @CacheEvict(value = {"allYuGiOhOfficialSets", "filteredYuGiOhOfficialSets", "officialSiteSet"}, allEntries = true)
    @RevisionMessage("Sauvegarde de l''extension {return}")
    public Ulid save(YuGiOhSetDTO dto) {
        var set = yuGiOhSetRepository.findByNullableId(dto.getId())
                .orElseGet(YuGiOhSet::new);

        yuGiOhSetMapper.update(set, dto);
        set.setSerie(yuGiOhSerieRepository.findById(dto.getSerieId()).orElseThrow(() -> new RuntimeException("Set not found")));

        var id = yuGiOhSetRepository.saveAndFlush(set).getId();

        officialSitePidService.setPids(id, dto.getOfficialSitePids());
        yugipediaSetService.setYugipediaSets(id, dto.getYugipediaSets());
        return id;
    }

    public List<YuGiOhSetDTO> finSavedSets(YuGiOhSetDTO set) {
        return set.getOfficialSitePids().stream()
                .map(p -> yuGiOhSetRepository.findByNullableId(officialSitePidService.getSetId(p)))
                .filter(Optional::isPresent)
                .map(s -> mapToDTO(s.get()))
                .toList();
    }

    public int getIdPca(Ulid setId) {
        if (setId == null) {
            return 0;
        }
        var set = yuGiOhSetRepository.findById(setId).orElse(null);

        if (set == null) {
            return 0;
        }
        var idPca = set.getIdPca();

        if (idPca == null) {
            idPca = buildIdPca(set);
        }
        return idPca;
    }

    private int buildIdPca(YuGiOhSet set) {
        if (!(set.getSerie() instanceof YuGiOhSerie serie)) {
            return 0;
        }

        var idPca = CardSetHelper.buildIdPca(serie.getIdPca(), yuGiOhSetRepository::existsByIdPca);

        set.setIdPca(idPca);
        yuGiOhSetRepository.save(set);
        revisionMessageService.addMessage("Création de l''ID PCA pour l''extension {0}", set.getId());
        return idPca;
    }
}
