package com.pcagrade.retriever.card.pokemon;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.merge.IMergeHistoryService;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.AbstractCardMergeService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

@Service
public class PokemonCardMergeService extends AbstractCardMergeService<PokemonCard> {

    public PokemonCardMergeService(@Nonnull PokemonCardRepository repository, @Nonnull IMergeHistoryService<Ulid> mergeHistoryService, @Nullable RevisionMessageService revisionMessageService) {
        super(repository, mergeHistoryService, revisionMessageService);
    }

    @Override
    protected void merge(PokemonCard target, PokemonCard source) {
        super.merge(target, source);
        PCAUtils.mergeCollections(PokemonCard::getChildren, PokemonCard::setParent, source, target);
    }
}
