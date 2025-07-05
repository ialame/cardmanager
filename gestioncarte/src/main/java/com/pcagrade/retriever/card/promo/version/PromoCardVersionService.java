package com.pcagrade.retriever.card.promo.version;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PromoCardVersionService {

    @Autowired
    private PromoCardVersionRepository promoCardVersionRepository;

    @Autowired
    private PromoCardVersionMapper promoCardVersionMapper;

    public List<PromoCardVersionDTO> findAll() {
        return promoCardVersionRepository.findAll().stream()
                .map(promoCardVersionMapper::mapToDTO)
                .toList();
    }

    public Optional<PromoCardVersionDTO> findById(Ulid id) {
        return promoCardVersionRepository.findById(id)
                .map(promoCardVersionMapper::mapToDTO);
    }

    @Transactional
    @RevisionMessage("Sauvegarde de la version de promotion {return}")
    public Ulid save(PromoCardVersionDTO dto) {
        var version = promoCardVersionRepository.getOrCreate(dto.getId(), PromoCardVersion::new);

        promoCardVersionMapper.update(version, dto);
        return promoCardVersionRepository.save(version).getId();
    }

}
