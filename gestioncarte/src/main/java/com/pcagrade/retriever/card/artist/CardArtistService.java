package com.pcagrade.retriever.card.artist;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.CardRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardArtistService {

    private static final Logger LOGGER = LogManager.getLogger(CardArtistService.class);

    @Autowired
    private CardArtistRepository cardArtistRepository;
    @Autowired
    private CardRepository cardRepository;

    public void setCardArtist(Ulid cardId, String artist) {
        var trimed = StringUtils.trimToEmpty(artist);

        if (StringUtils.isBlank(trimed)) {
            return;
        }

        var card = cardRepository.findById(cardId).orElse(null);

        if (card == null) {
            LOGGER.warn("Card not found with id {}", cardId);
            return;
        }

        card.setArtist(cardArtistRepository.findFirstByName(trimed).orElseGet(() -> {
            var a = new CardArtist();

            a.setName(trimed);
            cardArtistRepository.save(a);
            return a;
        }));
        cardRepository.save(card);
    }
}
