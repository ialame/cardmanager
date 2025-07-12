package com.pcagrade.painter.common.image;

import com.github.f4b6a3.ulid.Ulid;
import jakarta.annotation.Nonnull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

public interface IImageService {
    Optional<ImageDTO> findById(Ulid id);

    ImageDTO create(String folder, String source, boolean internal, @Nonnull byte[] sourceImage) throws IOException;

    ImageDTO create(String folder, String source, boolean internal, @Nonnull BufferedImage sourceImage) throws IOException;
}
