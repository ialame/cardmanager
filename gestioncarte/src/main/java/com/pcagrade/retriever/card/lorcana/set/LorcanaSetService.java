package com.pcagrade.retriever.card.lorcana.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.retriever.card.lorcana.serie.LorcanaSerieRepository;
import com.pcagrade.retriever.card.lorcana.source.mushu.set.MushuSetService;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LorcanaSetService {

    @Autowired
    private LorcanaSetRepository lorcanaSetRepository;
    @Autowired
    private LorcanaSerieRepository lorcanaSerieRepository;
    @Autowired
    private LorcanaSetMapper lorcanaSetMapper;
    @Autowired
    private MushuSetService mushuSetService;

    public List<LorcanaSetDTO> getSets() {
        return lorcanaSetRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public Optional<LorcanaSetDTO> findById(Ulid id) {
        return lorcanaSetRepository.findById(id)
                .map(this::mapToDTO);
    }

    @Nonnull
    private LorcanaSetDTO mapToDTO(LorcanaSet s) {
        var dto = lorcanaSetMapper.mapToDTO(s);

        dto.setMushuSets(mushuSetService.getMushuSets(dto.getId()));
        return dto;
    }

    @RevisionMessage("Sauvegarde de l''extension lorcana {return}")
    public Ulid save(LorcanaSetDTO dto) {
        var set = lorcanaSetRepository.getOrCreate(dto.getId(), LorcanaSet::new);

        lorcanaSetMapper.update(set, dto);
        set.setSerie(lorcanaSerieRepository.findById(dto.getSerieId())
                .orElseThrow(() -> new RuntimeException("Serie not found")));

        var id = lorcanaSetRepository.save(set).getId();

        mushuSetService.setMushuSets(id, dto.getMushuSets());
        return id;
    }
}
