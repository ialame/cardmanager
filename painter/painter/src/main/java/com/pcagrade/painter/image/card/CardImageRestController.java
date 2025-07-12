package com.pcagrade.painter.image.card;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.painter.common.image.card.CardImageDTO;
import com.pcagrade.painter.common.image.card.PublicCardImageDTO;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class CardImageRestController {

    public static final String READ_ROLE = "painter.pcagrade.com/images/cards.read";
    public static final String WRITE_ROLE = "painter.pcagrade.com/images/cards.write";

    private final CardImageService cardImageService;

    public CardImageRestController(CardImageService cardImageService) {
        this.cardImageService = cardImageService;
    }

    @RolesAllowed(READ_ROLE)
    @GetMapping("/api/images/cards/{cardId}")
    public List<CardImageDTO> findAllByCardId(@PathVariable("cardId") Ulid cardId) {
        return cardImageService.findAllByCardId(cardId);
    }

    @PermitAll
    @GetMapping("/public-api/images/cards/{cardId}")
    public List<PublicCardImageDTO> findAllPublicByCardId(@PathVariable("cardId") Ulid cardId) {
        return cardImageService.findAllPublicByCardId(cardId);
    }

    @RolesAllowed(WRITE_ROLE)
    @PostMapping("/api/images/cards")
    public void saveCardImage(@RequestBody List<CardImageDTO> images) {
        cardImageService.saveCardImages(images);
    }

    @RolesAllowed(WRITE_ROLE)
    @DeleteMapping("/api/images/cards/{cardId}/{localization}")
    public void deleteCardImage(@PathVariable("cardId") Ulid cardId, @PathVariable("localization") Localization localization) {
        cardImageService.deleteCardImage(cardId, localization);
    }

}
