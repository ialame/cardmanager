package com.pcagrade.retriever.card.lorcana.serie;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@CacheConfig(cacheNames = "LorcanaSerie")
public class LorcanaSerieService {

    @Autowired
    private LorcanaSerieRepository lorcanaSerieRepository;
    @Autowired
    private LorcanaSerieMapper lorcanaSerieMapper;

    @Cacheable
    public List<LorcanaSerieDTO> getSeries() {
        return lorcanaSerieRepository.findAll().stream()
                .map(lorcanaSerieMapper::mapToDTO)
                .toList();
    }

    @Cacheable
    public Optional<LorcanaSerieDTO> findById(Ulid id) {
        return lorcanaSerieRepository.findById(id)
                .map(lorcanaSerieMapper::mapToDTO);
    }

    @RevisionMessage("Sauvegarde de la serie lorcana {return}")
    @CacheEvict(cacheNames = {"LorcanaSerie"}, allEntries = true)
    public Ulid save(LorcanaSerieDTO dto) {
        var serie = lorcanaSerieRepository.findByNullableId(dto.getId())
                .orElseGet(LorcanaSerie::new);

        lorcanaSerieMapper.update(serie, dto);
        return lorcanaSerieRepository.save(serie).getId();
    }
}
