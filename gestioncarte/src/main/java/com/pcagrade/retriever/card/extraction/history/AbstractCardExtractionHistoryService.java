package com.pcagrade.retriever.card.extraction.history;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.set.CardSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public abstract class AbstractCardExtractionHistoryService<T> {

    private static final Logger LOGGER = LogManager.getLogger(AbstractCardExtractionHistoryService.class);

    @Autowired
    private CardExtractionHistoryRepository cardExtractionHistoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final ParameterizedTypeReference<T> type;

    protected AbstractCardExtractionHistoryService(Class<T> type) {
        this(ParameterizedTypeReference.forType(type));
    }

    protected AbstractCardExtractionHistoryService(ParameterizedTypeReference<T> type) {
        this.type = type;
    }

    @Transactional
    public void addHistory(T card) {
        try {
            var history = new CardExtractionHistory();

            history.setCard(objectMapper.writeValueAsString(card));
            history.setCardSets(getSet(card));
            cardExtractionHistoryRepository.save(history);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error while adding history", e);
        }
    }

    @Transactional
    public List<T> getExtractionHistory(Ulid setId) {
       return cardExtractionHistoryRepository.findAllByCardSetsId(setId).stream()
                .<T>mapMulti((h, downstream) -> {
                    try {
                        downstream.accept(objectMapper.readValue(h.getCard(), PCAUtils.getJavaType(objectMapper, type)));
                    } catch (JsonProcessingException e) {
                        LOGGER.error("Error while getting history", e);
                        cardExtractionHistoryRepository.deleteById(h.getId());
                    }
                })
               .toList();
    }

    @Transactional
    public void clearExtractionHistory(Ulid setId) {
        cardExtractionHistoryRepository.deleteAllByCardSetsId(setId);
    }

    protected abstract Set<CardSet> getSet(T card);

}
