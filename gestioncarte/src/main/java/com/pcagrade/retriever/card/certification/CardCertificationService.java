package com.pcagrade.retriever.card.certification;

import com.github.f4b6a3.ulid.Ulid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardCertificationService {

    @Autowired
    private CardCertificationRepository cardCertificationRepository;

    @Autowired
    private CardCertificationMapper cardCertificationMapper;

    public List<CardCertificationDTO>  findAllByCardId(Ulid id) {
        return cardCertificationRepository.findAllByCardId(id).stream()
                .map(cardCertificationMapper::mapToDTO)
                .toList();
    }

}
