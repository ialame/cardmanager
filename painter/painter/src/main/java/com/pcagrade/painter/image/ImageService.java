package com.pcagrade.painter.image;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.pcagrade.painter.common.image.IImageService;
import com.pcagrade.painter.common.image.ImageDTO;
import com.pcagrade.painter.common.image.ImageHelper;
import com.pcagrade.painter.common.path.PathHelper;
import com.pcagrade.painter.image.storage.ImageStorage;
import jakarta.annotation.Nonnull;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
public class ImageService implements IImageService {

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final ImageStorage imageStorage;


    public ImageService(ImageRepository imageRepository, ImageMapper imageMapper, ImageStorage imageStorage) {
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
        this.imageStorage = imageStorage;
    }

    @Override
    public Optional<ImageDTO> findById(Ulid id) {
        if (id == null) {
            return Optional.empty();
        }

        return imageRepository.findById(id)
                .map(imageMapper::mapToDTO);
    }

    @Override
    public ImageDTO create(String folder, String source, boolean internal, @Nonnull byte[] sourceImage) throws IOException {
        return create(folder, source, internal, ImageHelper.toBufferedImage(sourceImage));
    }

    @Override
    public ImageDTO create(String folder, String source, boolean internal, @Nonnull BufferedImage sourceImage) throws IOException {
        var imageId = UlidCreator.getUlid();// imageRepository.getNextImageId();
        var hash = StringUtils.substring(DigestUtils.md5Hex(ImageHelper.toByteArray(sourceImage, "png")), 0, 3);
        var path = PathHelper.cleanupPath(folder + "/" + hash + "/" + imageId + ".png");

        imageStorage.saveImage(path, sourceImage);

        var image = new Image();

        image.setPath(path);
        image.setSource(source);
        image.setInternal(internal);
        return imageMapper.mapToDTO(imageRepository.save(image));
    }
}
