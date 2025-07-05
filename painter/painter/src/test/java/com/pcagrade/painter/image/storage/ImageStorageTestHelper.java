package com.pcagrade.painter.image.storage;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class ImageStorageTestHelper {

    private ImageStorageTestHelper() {}

    public static String getImagePath(Class<?> testClass) {
        return testClass.getSimpleName() + "/";
    }

    public static String getImageStoragePath(Class<?> testClass) {
        return "target/test-images/" + getImagePath(testClass);
    }

    public static void delete(Class<?> testClass) {
        try {
            FileUtils.deleteDirectory(new File(getImageStoragePath(testClass)));
        } catch (Exception e) {
            // ignore
        }
    }
}
