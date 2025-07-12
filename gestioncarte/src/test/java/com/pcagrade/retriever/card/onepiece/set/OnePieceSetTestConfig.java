package com.pcagrade.retriever.card.onepiece.set;

import com.pcagrade.mason.jpa.revision.message.RevisionMessageConfiguration;
import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.onepiece.OnePieceFieldMappingService;
import com.pcagrade.retriever.card.onepiece.set.translation.OnePieceSetTranslationMapper;
import com.pcagrade.retriever.card.onepiece.set.translation.OnePieceSetTranslationMapperImpl;
import com.pcagrade.retriever.card.onepiece.source.official.id.OnePieceOfficialSiteIdTestConfig;
import com.pcagrade.retriever.field.mapper.FieldMappingService;
import com.pcagrade.retriever.field.mapper.FieldMappingTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

@RetrieverTestConfiguration
@Import({RevisionMessageConfiguration.class, OnePieceOfficialSiteIdTestConfig.class, FieldMappingTestConfig.class})
public class OnePieceSetTestConfig {

    public static final List<OnePieceSet> LIST = List.of(
            OnePieceSetTestProvider.op02()
    );

    @Bean
    public OnePieceFieldMappingService onePieceFieldMappingService(FieldMappingService fieldMappingService) {
        return new OnePieceFieldMappingService(fieldMappingService);
    }

    @Bean
    public OnePieceSetRepository onePieceSetRepository() {
        return RetrieverTestUtils.mockRepository(OnePieceSetRepository.class, LIST, OnePieceSet::getId);
    }

    @Bean
    public OnePieceSetMapper onePieceSetMapper() {
        return new OnePieceSetMapperImpl();
    }

    @Bean
    public OnePieceSetTranslationMapper onePieceSetTranslationMapper() {
        return new OnePieceSetTranslationMapperImpl();
    }

    @Bean
    public OnePieceSetService onePieceSetService() {
        return new OnePieceSetService();
    }
}
