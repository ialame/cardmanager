package com.pcagrade.painter.image.legacy;

import com.pcagrade.painter.common.image.ImageDTO;
import com.pcagrade.painter.common.image.legacy.ILegacyImageService;
import com.pcagrade.painter.common.path.PathHelper;
import com.pcagrade.painter.image.ImageService;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
public class LegacyImageService implements ILegacyImageService {

    private final LegacyImageStorage legacyImageStorage;
    private final ImageService imageService;

    public LegacyImageService(LegacyImageStorage legacyImageStorage, ImageService imageService) {
        this.legacyImageStorage = legacyImageStorage;
        this.imageService = imageService;
    }

    @Override
    public String findById(String legacyId) throws IOException {
        return PathHelper.cleanupPath( "https://pcagrade.com/images/" /* TODO config */ + legacyImageStorage.findImagePath(legacyId).replace("\\", "/"));
    }

    @Override
    public Optional<ImageDTO> restoreImage(String folder, String path, boolean internal) {
        if (StringUtils.isBlank(path)) {
            return Optional.empty();
        }
        try {
            return legacyImageStorage.getImage(path)
                    .map(file -> {
                        try {
                            return imageService.create(folder, path, internal, Files.readAllBytes(file.toPath()));
                        } catch (IOException e) {
                            throw new LegacyImageRestorationException(e);
                        }
                    });
        } catch (IOException e) {
            throw new LegacyImageRestorationException(e);
        }
    }
}
