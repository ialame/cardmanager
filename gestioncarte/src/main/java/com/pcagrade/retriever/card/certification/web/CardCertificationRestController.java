package com.pcagrade.retriever.card.certification.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.certification.CardCertificationDTO;
import com.pcagrade.retriever.card.certification.CardCertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cards/")
public class CardCertificationRestController {

    @Autowired
    private CardCertificationService cardCertificationService;

    @GetMapping("/{id}/certifications")
    public List<CardCertificationDTO> findAllByCardId(@PathVariable Ulid id) {
        return cardCertificationService.findAllByCardId(id);
    }
}
