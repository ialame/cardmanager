package com.pcagrade.retriever.card.promo.event;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.merge.IMergeHistoryService;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitMapper;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitMapperImpl;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitMergeService;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitRepository;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitService;
import com.pcagrade.retriever.card.promo.event.trait.translation.PromoCardEventTraitTranslationMapper;
import com.pcagrade.retriever.card.promo.event.trait.translation.PromoCardEventTraitTranslationMapperImpl;
import com.pcagrade.retriever.card.promo.event.translation.PromoCardEventTranslationMapper;
import com.pcagrade.retriever.card.promo.event.translation.PromoCardEventTranslationMapperImpl;
import com.pcagrade.retriever.merge.MergeTestConfig;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

@RetrieverTestConfiguration
@Import({MergeTestConfig.class})
public class PromoCardEventTestConfig {

    @Bean
    public PromoCardEventRepository promoCardEventRepository() {
        var list = List.of(PromoCardEventTestProvider.testEvent());

        return RetrieverTestUtils.mockRepository(PromoCardEventRepository.class, list, PromoCardEvent::getId);
    }

    @Bean
    public PromoCardEventTraitRepository promoCardEventTraitRepository() {
        return RetrieverTestUtils.mockRepository(PromoCardEventTraitRepository.class);
    }

    @Bean
    public PromoCardEventMapper promoCardEventMapper() {
        return new PromoCardEventMapperImpl();
    }

    @Bean
    public PromoCardEventTranslationMapper promoCardEventTranslationMapper() {
        return new PromoCardEventTranslationMapperImpl();
    }

    @Bean
    public PromoCardEventTraitTranslationMapper promoCardEventTraitTranslationMapper() {
        return new PromoCardEventTraitTranslationMapperImpl();
    }

    @Bean
    public PromoCardEventTraitMapper promoCardEventTraitMapper() {
        return new PromoCardEventTraitMapperImpl();
    }

    @Bean
    public PromoCardEventTraitMergeService promoCardEventTraitMergeService(@Nonnull PromoCardEventTraitRepository promoCardEventTraitRepository, @Nonnull IMergeHistoryService<Ulid> mergeHistoryService, @Nullable RevisionMessageService revisionMessageService) {
        return new PromoCardEventTraitMergeService(promoCardEventTraitRepository, mergeHistoryService, revisionMessageService);
    }

    @Bean
    public PromoCardEventTraitService promoCardEventTraitService() {
        return new PromoCardEventTraitService();
    }

    @Bean
    public PromoCardEventService promoCardEventService() {
        return new PromoCardEventService();
    }
}
