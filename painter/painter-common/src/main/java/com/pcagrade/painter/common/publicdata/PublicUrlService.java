package com.pcagrade.painter.common.publicdata;

import com.pcagrade.painter.common.path.PathHelper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class PublicUrlService {

    private final String publicUrl;

    public PublicUrlService(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    @Nonnull
    public String buildPublicUrl(@Nullable String path) {
        if (StringUtils.isBlank(path)) {
            return "";
        }
        return PathHelper.cleanupPath(publicUrl + '/' + path);
    }

}
