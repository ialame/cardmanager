package com.pcagrade.painter.publicdata;

import com.pcagrade.painter.common.publicdata.PublicUrlService;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractPublicImageMapper {

    @Autowired
    private PublicUrlService publicUrlService;

    @BuildPublicUrl
    protected String buildPublicUrl(String path) {
        return publicUrlService.buildPublicUrl(path);
    }
}
