package com.pcagrade.painter.image.card;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.painter.common.image.card.CardImageDTO;
import com.pcagrade.painter.common.image.card.ICardImageService;
import com.pcagrade.painter.common.image.card.PublicCardImageDTO;
import com.pcagrade.painter.image.ImageRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class CardImageService implements ICardImageService {

    private final ImageRepository imageRepository;
    private final CardImageRepository cardImageRepository;
    private final CardImageMapper cardImageMapper;
    private final RevisionMessageService revisionMessageService;

    public CardImageService(ImageRepository imageRepository, CardImageRepository cardImageRepository, CardImageMapper cardImageMapper, RevisionMessageService revisionMessageService) {
        this.imageRepository = imageRepository;
        this.cardImageRepository = cardImageRepository;
        this.cardImageMapper = cardImageMapper;
        this.revisionMessageService = revisionMessageService;
    }

    @Override
    @Nonnull
    public List<CardImageDTO> findAllByCardId(@Nullable Ulid cardId) {
        if (cardId == null) {
            return Collections.emptyList();
        }

        return cardImageRepository.findAllByCardId(cardId).stream()
                .map(cardImageMapper::mapToDTO)
                .toList();
    }

    @Override
    @Nonnull
    public List<PublicCardImageDTO> findAllPublicByCardId(@Nullable Ulid cardId) {
        if (cardId == null) {
            return Collections.emptyList();
        }

        return cardImageRepository.findAllByCardId(cardId).stream()
                .filter(c -> !c.getImage().isInternal())
                .map(cardImageMapper::mapToPublicDTO)
                .toList();
    }

    @Override
    public void saveCardImage(@Nonnull CardImageDTO dto) {
        var cardImage = cardImageRepository.findFirstByCardIdAndLocalization(dto.cardId(), dto.localization())
                .orElseGet(CardImage::new);
        var image = imageRepository.findById(dto.imageId())
                .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + dto.imageId()));

        cardImageMapper.updateFromDTO(cardImage, dto);
        cardImage.setImage(image);

        cardImageRepository.save(cardImage);
        revisionMessageService.addMessage("Sauvegarde de l''image pour la carte {0} ({1})", dto.cardId(), dto.localization());
    }

    @Override
    public void saveCardImages(@Nonnull Iterable<CardImageDTO> dto) {

        dto.forEach(this::saveCardImage);
    }

    @Override
    @RevisionMessage("Suppression de l''image pour la carte {0} ({1})")
    public void deleteCardImage(@Nonnull Ulid cardId, @Nonnull Localization localization) {
        cardImageRepository.deleteAllByCardIdAndLocalization(cardId, localization);
    }
}
