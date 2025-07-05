package com.pcagrade.retriever.card.onepiece;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.merge.IMergeHistoryService;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.retriever.card.AbstractCardMergeService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

@Service
public class OnePieceCardMergeService extends AbstractCardMergeService<OnePieceCard> {
    public OnePieceCardMergeService(@Nonnull OnePieceCardRepository repository, @Nonnull IMergeHistoryService<Ulid> mergeHistoryService, @Nullable RevisionMessageService revisionMessageService) {
        super(repository, mergeHistoryService, revisionMessageService);
    }
}
