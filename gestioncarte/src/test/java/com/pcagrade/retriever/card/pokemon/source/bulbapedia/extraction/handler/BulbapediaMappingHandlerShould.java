package com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.handler;

import com.pcagrade.retriever.annotation.RetrieverTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(BulbapediaMappingHandlerTestConfig.class)
class BulbapediaMappingHandlerShould {

    @Autowired(required = false)
    private List<IBulbapediaMappingHandler> mappingHandlers;

    @Test
    void beOrdered() {
        assertThat(mappingHandlers).isNotNull().isNotEmpty();
        assertThat(mappingHandlers.get(0)).isInstanceOf(BulbapediaMappingFeatureHandler.class);
        assertThat(mappingHandlers.get(1)).isInstanceOf(BulbapediaMappingBracketHandler.class);
        assertThat(mappingHandlers.get(2)).isInstanceOf(BulbapediaMappingFormeHandler.class);
        assertThat(mappingHandlers.get(3)).isInstanceOf(BulbapediaMappingRegionalFormHandler.class);
    }
}
