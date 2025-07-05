package com.pcagrade.painter.image.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.painter.common.image.set.ISetImageService;
import com.pcagrade.painter.common.image.set.PublicSetImageDTO;
import com.pcagrade.painter.common.image.set.SetImageDTO;
import com.pcagrade.painter.image.ImageRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class SetImageService implements ISetImageService {

    private final ImageRepository imageRepository;
    private final SetImageRepository setImageRepository;
    private final SetImageMapper setImageMapper;
    private final RevisionMessageService revisionMessageService;

    public SetImageService(ImageRepository imageRepository, SetImageRepository setImageRepository, SetImageMapper setImageMapper, RevisionMessageService revisionMessageService) {
        this.imageRepository = imageRepository;
        this.setImageRepository = setImageRepository;
        this.setImageMapper = setImageMapper;
        this.revisionMessageService = revisionMessageService;
    }

    @Override
    @Nonnull
    public List<SetImageDTO> findAllBySetId(@Nullable Ulid setId) {
        if (setId == null) {
            return Collections.emptyList();
        }

        return setImageRepository.findAllBySetId(setId).stream()
                .map(setImageMapper::mapToDTO)
                .toList();
    }

    @Override
    @Nonnull
    public List<PublicSetImageDTO> findAllPublicBySetId(@Nullable Ulid setId) {
        if (setId == null) {
            return Collections.emptyList();
        }

        return setImageRepository.findAllBySetId(setId).stream()
                .filter(c -> !c.getImage().isInternal())
                .map(setImageMapper::mapToPublicDTO)
                .toList();
    }

    @Override
    public void saveSetImage(@Nonnull SetImageDTO dto) {
        var setImage = setImageRepository.findFirstBySetIdAndLocalization(dto.setId(), dto.localization())
                .orElseGet(SetImage::new);
        var image = imageRepository.findById(dto.imageId())
                .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + dto.imageId()));

        setImageMapper.updateFromDTO(setImage, dto);
        setImage.setImage(image);
        setImageRepository.save(setImage);
        revisionMessageService.addMessage("Sauvegarde de l''image pour l''extension {0} ({1})", dto.setId(), dto.localization());
    }

    @Override
    public void saveSetImages(@Nonnull Iterable<SetImageDTO> dto) {
        dto.forEach(this::saveSetImage);
    }

    @Override
    @RevisionMessage("Suppression de l''image pour l''extension {0} ({1})")
    public void deleteSetImage(@Nonnull Ulid setId, @Nonnull Localization localization) {
        setImageRepository.deleteAllBySetIdAndLocalization(setId, localization);
    }
}
