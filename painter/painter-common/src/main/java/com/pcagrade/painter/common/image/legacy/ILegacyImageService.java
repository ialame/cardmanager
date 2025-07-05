package com.pcagrade.painter.common.image.legacy;

import com.pcagrade.painter.common.image.ImageDTO;

import java.io.IOException;
import java.util.Optional;

public interface ILegacyImageService {
    String findById(String legacyId) throws IOException;

    Optional<ImageDTO> restoreImage(String folder, String path, boolean internal);
}
