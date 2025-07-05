package com.pcagrade.retriever.card.promo.event.trait;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.merge.AbstractMergeService;
import com.pcagrade.mason.jpa.merge.IMergeHistoryService;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.promo.event.PromoCardEvent;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

@Service
public class PromoCardEventTraitMergeService extends AbstractMergeService<PromoCardEventTrait, Ulid> {

    public PromoCardEventTraitMergeService(@Nonnull PromoCardEventTraitRepository repository, @Nonnull IMergeHistoryService<Ulid> mergeHistoryService, @Nullable RevisionMessageService revisionMessageService) {
        super(repository, mergeHistoryService, revisionMessageService, PromoCardEventTrait::getId);
    }

    @Override
    protected void merge(PromoCardEventTrait target, PromoCardEventTrait source) {
        PCAUtils.mergeCollections(PromoCardEventTrait::getEvents, PromoCardEvent::getTraits, source, target);
    }
}
