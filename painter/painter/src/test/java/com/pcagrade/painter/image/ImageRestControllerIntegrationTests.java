package com.pcagrade.painter.image;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.test.oauth2.params.AnonymousRequestSource;
import com.pcagrade.mason.test.oauth2.params.JwtRequestSource;
import com.pcagrade.mason.test.oauth2.params.JwtRequestSources;
import com.pcagrade.painter.PainterIntegrationTest;
import com.pcagrade.painter.image.storage.ImageStorageTestHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static com.pcagrade.mason.test.oauth2.OAuth2PostProcessors.jwtWithRoles;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@PainterIntegrationTest
@AutoConfigureMockMvc
public class ImageRestControllerIntegrationTests {

    private static final String PATH = ImageStorageTestHelper.getImagePath(ImageRestControllerIntegrationTests.class);
    public static final Ulid TEST_IMAGE_ID = Ulid.from("01HKMMHDQZHP7SNZCNW5QDA740");

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:logo.png")
    private Resource logo;

    @BeforeAll
    public static void beforeAll() {
        ImageStorageTestHelper.delete(ImageRestControllerIntegrationTests.class);
    }
    @ParameterizedTest
    @AnonymousRequestSource
    @JwtRequestSources(@JwtRequestSource)
    void get_should_beForbidden(RequestPostProcessor postProcessor) throws Exception {
        mockMvc.perform(get("/api/images/{id}", TEST_IMAGE_ID)
                        .with(postProcessor))
                .andExpect(status().isForbidden());
    }

    @Test
    void get_should_returnImage_when_userHasRoles() throws Exception {
        mockMvc.perform(get("/api/images/{id}", TEST_IMAGE_ID)
                        .with(jwtWithRoles(ImageRestController.READ_ROLE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_IMAGE_ID.toString()))
                .andExpect(jsonPath("$.path").value("/test/url/image.png"))
                .andExpect(jsonPath("$.internal").value(false))
                .andExpect(jsonPath("$.source").doesNotExist());
    }

    @ParameterizedTest
    @AnonymousRequestSource
    @JwtRequestSource
    @JwtRequestSource(ImageRestController.READ_ROLE)
    void upload_should_beForbidden(RequestPostProcessor postProcessor) throws Exception {
        mockMvc.perform(multipart("/api/images/")
                        .file("file", logo.getContentAsByteArray())
                        .param("path", (PATH + "/test/url/"))
                        .param("source", "test")
                        .param("internal", "false")
                        .with(postProcessor))
                .andExpect(status().isForbidden());
    }

    @Test
    void upload_should_createImage_when_userHasRoles() throws Exception {
        var asyncResult = mockMvc.perform(multipart("/api/images/")
                        .file("file", logo.getContentAsByteArray())
                        .param("path", (PATH + "/test/url/"))
                        .param("source", "test")
                        .param("internal", "false")
                        .with(jwtWithRoles(ImageRestController.WRITE_ROLE)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(asyncResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.path").value(PATH + "test/url/316/202700.png"))
                .andExpect(jsonPath("$.internal").value(false))
                .andExpect(jsonPath("$.source").value("test"));

        mockMvc.perform(get("/images/" + PATH + "/test/url/316/202700.png")
                        .with(jwtWithRoles(ImageRestController.READ_ROLE)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"));
    }

    @AfterAll
    public static void afterAll() {
        ImageStorageTestHelper.delete(ImageRestControllerIntegrationTests.class);
    }
}
