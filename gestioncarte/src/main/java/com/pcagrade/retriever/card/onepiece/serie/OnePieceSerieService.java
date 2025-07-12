package com.pcagrade.retriever.card.onepiece.serie;

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
@CacheConfig(cacheNames = "onePieceSerie")
public class OnePieceSerieService {

    @Autowired
    private OnePieceSerieRepository onePieceSerieRepository;
    @Autowired
    private OnePieceSerieMapper onePieceSerieMapper;

    @Cacheable
    public List<OnePieceSerieDTO> getSeries() {
        return onePieceSerieRepository.findAll().stream()
                .map(onePieceSerieMapper::mapToDTO)
                .toList();
    }

    @Cacheable
    public Optional<OnePieceSerieDTO> findById(Ulid id) {
        return onePieceSerieRepository.findById(id)
                .map(onePieceSerieMapper::mapToDTO);
    }

    @Cacheable
    public Optional<OnePieceSerieDTO> findByName(String name) {
        return onePieceSerieRepository.findByName(name)
                .map(onePieceSerieMapper::mapToDTO);
    }

    @RevisionMessage("Sauvegarde de la serie one piece {return}")
    @CacheEvict(cacheNames = {"onePieceSerie", "onePieceOfficialSiteService", "onePieceOfficialSiteParser"}, allEntries = true)
    public Ulid save(OnePieceSerieDTO dto) {
        var serie = onePieceSerieRepository.findByNullableId(dto.getId())
                .orElseGet(OnePieceSerie::new);

        onePieceSerieMapper.update(serie, dto);
        return onePieceSerieRepository.save(serie).getId();
    }
}
