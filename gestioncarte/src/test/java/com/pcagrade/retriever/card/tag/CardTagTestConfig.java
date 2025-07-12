package com.pcagrade.retriever.card.tag;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.tag.translation.CardTagTranslationMapper;
import com.pcagrade.retriever.card.tag.translation.CardTagTranslationMapperImpl;
import com.pcagrade.retriever.card.tag.type.CardTagType;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({CardTagConfiguration.class})
public class CardTagTestConfig {

    @Bean
    public CardTagRepository cardTagRepository() {
        var repo = RetrieverTestUtils.mockRepository(CardTagRepository.class, CardTagTestProvider.LIST, CardTag::getId);

        Mockito.when(repo.findByTypeUsName(Mockito.any(CardTagType.class), Mockito.anyString())).then(invocation -> {
            var type = invocation.getArgument(0, CardTagType.class);
            var name = invocation.getArgument(1, String.class);

            return CardTagTestProvider.LIST.stream()
                    .filter(t -> type == t.getType() && StringUtils.equalsIgnoreCase(t.getTranslations().get(Localization.USA).getName(), name))
                    .findFirst();
        });
        return repo;
    }

    @Bean
    public CardTagTranslationMapper cardTagTranslationMapper() {
        return new CardTagTranslationMapperImpl();
    }

    @Bean
    public CardTagMapper cardTagMapper() {
        return new CardTagMapperImpl();
    }

    @Bean
    public CardTagService cardTagService() {
        return new CardTagService();
    }

}
