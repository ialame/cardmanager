package com.pcagrade.painer.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.UlidCreator;
import com.pcagrade.mason.web.client.WebClientConfigurer;
import com.pcagrade.painter.common.image.IImageService;
import com.pcagrade.painter.common.image.ImageDTO;
import jakarta.annotation.Nonnull;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@Import(PainterTestConfiguration.class)
class ClientImageServiceTests {

    private static MockWebServer mockBackEnd;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebClientConfigurer webClientConfigurer;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void findById_should_returnEmpty_whenNull() {
        var imageService = createImageService();
        var opt = imageService.findById(null);

        assertThat(opt).isEmpty();
    }

    @Test
    void findById_should_returnImage_withValidId() throws Exception {
        var imageService = createImageService();
        var ulid = UlidCreator.getMonotonicUlid();
        var image = new ImageDTO(ulid, "folder", "source", Instant.now(), false);

        enqueueResponse(image);

        var opt = imageService.findById(ulid);
        var request = mockBackEnd.takeRequest();

        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/api/images/" + ulid);
        assertThat(opt).isNotEmpty().hasValue(image);
    }

    @Test
    void create_should_createImage() throws Exception {
        var imageService = createImageService();
        var ulid = UlidCreator.getMonotonicUlid();
        var image = new ImageDTO(ulid, "folder", "source", Instant.now(), false);

        enqueueResponse(image);

        var i = imageService.create("folder", "source", false, new byte[0]);
        var request = mockBackEnd.takeRequest();

        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/api/images/");
        assertThat(i).isNotNull().isEqualTo(image);
    }

    private void enqueueResponse(ImageDTO image) throws JsonProcessingException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(image))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }

    @Nonnull
    private IImageService createImageService() {
        return new ClientImageService(new PainterClientProperties(mockBackEnd.url("/").toString(), null, "test"), webClientConfigurer);
    }
}
