package com.pcagrade.retriever.card.onepiece;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.merge.IMergeHistoryService;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.artist.CardArtistTestConfig;
import com.pcagrade.retriever.card.extraction.status.CardExtractionStatusTestConfig;
import com.pcagrade.retriever.card.onepiece.serie.OnePieceSerieTestConfig;
import com.pcagrade.retriever.card.onepiece.translation.OnePieceCardTranslationMapper;
import com.pcagrade.retriever.card.onepiece.translation.OnePieceCardTranslationMapperImpl;
import com.pcagrade.retriever.merge.MergeTestConfig;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Collections;

@RetrieverTestConfiguration
@Import({OnePieceSerieTestConfig.class, CardExtractionStatusTestConfig.class, CardArtistTestConfig.class, MergeTestConfig.class})
public class OnePieceCardTestConfig {

    @Bean
    public OnePieceCardRepository onePieceCardRepository() {
        var repository = RetrieverTestUtils.mockRepository(OnePieceCardRepository.class);

        Mockito.when(repository.findAllBySetIdAndNumber(Mockito.any(Ulid.class), Mockito.anyString())).thenReturn(Collections.emptyList());
        return repository;
    }

    @Bean
    public OnePieceCardTranslationMapper onePieceCardTranslationMapper() {
        return new OnePieceCardTranslationMapperImpl();
    }

    @Bean
    public OnePieceCardMapper onePieceCardMapper() {
        return new OnePieceCardMapperImpl();
    }

    @Bean
    public OnePieceCardMergeService onePieceCardMergeService(@Nonnull OnePieceCardRepository onePieceCardRepository, @Nonnull IMergeHistoryService<Ulid> mergeHistoryService, @Nullable RevisionMessageService revisionMessageService) {
        return new OnePieceCardMergeService(onePieceCardRepository, mergeHistoryService, revisionMessageService);
    }

    @Bean
    public OnePieceCardService onePieceCardService() {
        return new OnePieceCardService();
    }
}
