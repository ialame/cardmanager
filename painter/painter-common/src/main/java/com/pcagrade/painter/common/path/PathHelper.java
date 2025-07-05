package com.pcagrade.painter.common.path;


import org.apache.commons.lang3.StringUtils;

public class PathHelper {

    private PathHelper() { }

    public static String cleanupPath(String path) {
        if (StringUtils.isBlank(path)) {
            return "";
        }

        return StringUtils.trimToEmpty(path.replaceAll("(?<!:)/{2,}", "/"));
    }
}
