package com.pcagrade.retriever.card;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.merge.AbstractMergeService;
import com.pcagrade.mason.jpa.merge.IMergeHistoryService;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.certification.CardCertification;
import com.pcagrade.retriever.card.image.CardImage;
import com.pcagrade.retriever.card.price.AveragePrice;
import com.pcagrade.retriever.card.saved.SavedCard;
import com.pcagrade.retriever.card.tag.CardTag;
import com.pcagrade.retriever.cart.item.CartItem;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public abstract class AbstractCardMergeService<T extends Card> extends AbstractMergeService<T, Ulid> {

    protected AbstractCardMergeService(@Nonnull MasonRevisionRepository<T, Ulid> repository, @Nonnull IMergeHistoryService<Ulid> mergeHistoryService, @Nullable RevisionMessageService revisionMessageService) {
        super(repository, mergeHistoryService, revisionMessageService, Card::getId);
    }

    @Override
    protected void merge(T target, T source) {
        if (target.getPrice() == null) {
            AveragePrice price = source.getPrice();

            if (price != null) {
                target.setPrice(price);
            }
        }
        PCAUtils.mergeCollections(Card::getItems, CartItem::setCard, source, target);
        PCAUtils.mergeCollections(Card::getCertifications, CardCertification::setCard, source, target);
        PCAUtils.mergeCollections(Card::getImages, CardImage::setCard, source, target);
        PCAUtils.mergeCollections(Card::getSavedCards, SavedCard::setCard, source, target);
        PCAUtils.mergeCollections(Card::getCardTags, CardTag::getCards, source, target);
    }
}
