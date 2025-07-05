package com.pcagrade.retriever.card.pokemon;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.merge.IMergeHistoryService;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.artist.CardArtistTestConfig;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion.ExpansionBulbapediaTestConfig;
import com.pcagrade.retriever.card.pokemon.tag.PokemonCardTagService;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationServiceTestConfig;
import com.pcagrade.retriever.card.tag.CardTagTestConfig;
import com.pcagrade.retriever.merge.MergeTestConfig;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({ PokemonCardMapperTestConfig.class, PokemonCardTranslationServiceTestConfig.class, ExpansionBulbapediaTestConfig.class, CardTagTestConfig.class, CardArtistTestConfig.class, RevisionMessageService.class, PokemonCardRepositoryTestConfig.class, MergeTestConfig.class })
public class PokemonCardServiceTestConfig {

    @Bean
    public PokemonCardMergeService pokemonCardMergeService(@Nonnull PokemonCardRepository pokemonCardRepository, @Nonnull IMergeHistoryService<Ulid> mergeHistoryService, @Nullable RevisionMessageService revisionMessageService) {
        return new PokemonCardMergeService(pokemonCardRepository, mergeHistoryService, revisionMessageService);
    }

    @Bean
    public PokemonCardService pokemonCardService() {
        return new PokemonCardService();
    }

    @Bean
    public PokemonCardTagService pokemonCardTagService() {
        return new PokemonCardTagService();
    }
}
