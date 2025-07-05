package com.pcagrade.painter.image.storage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig
class ImageStorageTests {

    private static final String PATH = ImageStorageTestHelper.getImageStoragePath(ImageStorageTests.class);

    @Value("classpath:logo.png")
    private Resource logo;

    private final ImageStorage imageStorage = new ImageStorage(PATH);

    @BeforeAll
    public static void beforeAll() {
        ImageStorageTestHelper.delete(ImageStorageTests.class);
    }

    @Test
    void save_should_saveFile() throws IOException {
        var content = logo.getContentAsByteArray();

        imageStorage.saveImage("logo.png", content);
        assertThat(imageStorage.getFile("logo.png")).exists()
                .isFile()
                .hasBinaryContent(content);
    }

    @Test
    void saveBase64_should_saveFile() throws IOException {
        var content = logo.getContentAsByteArray();
        var base64 = Base64.getEncoder().encodeToString(content);

        imageStorage.saveBase64Image("logoBase64.png", base64);
        assertThat(imageStorage.getFile("logoBase64.png")).exists()
                .isFile()
                .hasBinaryContent(content);
    }

    @Test
    void saveImage_should_saveFile() throws IOException {
        var content = logo.getContentAsByteArray();
        var image = ImageIO.read(logo.getInputStream());

        imageStorage.saveImage("logoImage.png", image);
        assertThat(imageStorage.getFile("logoImage.png")).exists()
                .isFile()
                .hasBinaryContent(content);
    }

    @Test
    void getFile_should_throwSecurityException_when_fileIsNotInStoragePath() {
        assertThatThrownBy(() -> imageStorage.getFile("../logoInvalid.png"))
                .isInstanceOf(SecurityException.class);
    }

    @AfterAll
    public static void afterAll() {
        ImageStorageTestHelper.delete(ImageStorageTests.class);
    }
}
