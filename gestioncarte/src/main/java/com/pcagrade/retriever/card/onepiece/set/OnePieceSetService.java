package com.pcagrade.retriever.card.onepiece.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.retriever.card.onepiece.serie.OnePieceSerie;
import com.pcagrade.retriever.card.onepiece.serie.OnePieceSerieRepository;
import com.pcagrade.retriever.card.onepiece.source.official.id.OnePieceOfficialSiteIdService;
import com.pcagrade.retriever.card.set.CardSetHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OnePieceSetService {

    @Autowired
    private OnePieceSetRepository onePieceSetRepository;
    @Autowired
    private OnePieceSerieRepository onePieceSerieRepository;
    @Autowired
    private OnePieceSetMapper onePieceSetMapper;
    @Autowired
    private OnePieceOfficialSiteIdService onePieceOfficialSiteIdService;
    @Autowired
    private RevisionMessageService revisionMessageService;

    public List<OnePieceSetDTO> getSets() {
        return onePieceSetRepository.findAll().stream()
                .map(onePieceSetMapper::mapToDTO)
                .toList();
    }

    public Optional<OnePieceSetDTO> findById(Ulid id) {
        return onePieceSetRepository.findById(id)
                .map(onePieceSetMapper::mapToDTO);
    }

    @RevisionMessage("Sauvegarde de l''extension one piece {return}")
    public Ulid save(OnePieceSetDTO dto) {
        var set = onePieceSetRepository.getOrCreate(dto.getId(), OnePieceSet::new);

        onePieceSetMapper.update(set, dto);
        set.setSerie(onePieceSerieRepository.findById(dto.getSerieId())
                .orElseThrow(() -> new RuntimeException("Serie not found")));

        var id = onePieceSetRepository.save(set).getId();

        onePieceOfficialSiteIdService.setIds(id, dto.getOfficialSiteIds());
        return id;
    }

    public int getIdPca(Ulid setId) {
        if (setId == null) {
            return 0;
        }
        var set = onePieceSetRepository.findById(setId).orElse(null);

        if (set == null) {
            return 0;
        }
        var idPca = set.getIdPca();

        if (idPca == null) {
            idPca = buildIdPca(set);
        }
        return idPca;
    }

    private int buildIdPca(OnePieceSet set) {
        if (!(set.getSerie() instanceof OnePieceSerie serie)) {
            return 0;
        }

        var idPca = CardSetHelper.buildIdPca(serie.getIdPca(), onePieceSetRepository::existsByIdPca);

        set.setIdPca(idPca);
        onePieceSetRepository.save(set);
        revisionMessageService.addMessage("Création de l''ID PCA pour l''extension {0}", set.getId());
        return idPca;
    }
}
