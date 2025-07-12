package com.pcagrade.retriever.card.tag;

import com.pcagrade.retriever.card.tag.type.CardTagTypeConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CardTagConfiguration {

    @Bean
    public CardTagTypeConverter cardTagTypeConverter() {
        return new CardTagTypeConverter();
    }
}
