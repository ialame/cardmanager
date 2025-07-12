package com.pcagrade.painter.image.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.test.oauth2.params.AnonymousRequestSource;
import com.pcagrade.mason.test.oauth2.params.JwtRequestSource;
import com.pcagrade.mason.test.oauth2.params.JwtRequestSources;
import com.pcagrade.painter.PainterIntegrationTest;
import com.pcagrade.painter.image.ImageRestControllerIntegrationTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static com.pcagrade.mason.test.oauth2.OAuth2PostProcessors.jwtWithRoles;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@PainterIntegrationTest
@AutoConfigureMockMvc
class SetImageRestControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    private static final Ulid TEST_SET_IMAGE_ID = Ulid.from("01HSDRVGE5GAJDBZBC75PT19B2");
    private static final Ulid TEST_SET_ID = Ulid.from("01HSDRW05T9KNGQ5DWP0FH22GJ");
    private static final Ulid TEST_SET_ID_2 = Ulid.from("01HSDRWD40724K2FZDEYENNZ6H");
    private static final String SAVE_SET_CONTENT = "[{\"setId\":\"" + TEST_SET_ID + "\",\"localization\":\"fr\",\"imageId\":\"" + ImageRestControllerIntegrationTests.TEST_IMAGE_ID + "\"}]";

    @ParameterizedTest
    @AnonymousRequestSource
    @JwtRequestSources(@JwtRequestSource)
    void findAllBySetId_should_beForbidden(RequestPostProcessor postProcessor) throws Exception {
        mockMvc.perform(get("/api/images/sets/{id}", TEST_SET_ID)
                        .with(postProcessor))
                .andExpect(status().isForbidden());
    }

    @Test
    void findAllBySetId_should_returnSetImage_when_userHasRoles() throws Exception {
        mockMvc.perform(get("/api/images/sets/{id}", TEST_SET_ID)
                        .with(jwtWithRoles(SetImageRestController.READ_ROLE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].setId").value(TEST_SET_ID.toString()))
                .andExpect(jsonPath("$[0].localization").value(Localization.USA.getCode()))
                .andExpect(jsonPath("$[0].imageId").value(ImageRestControllerIntegrationTests.TEST_IMAGE_ID.toString()));
    }

    @ParameterizedTest
    @AnonymousRequestSource
    @JwtRequestSource
    @JwtRequestSource(SetImageRestController.READ_ROLE)
    void findAllPublicBySetId_should_returnPublicSetImage(RequestPostProcessor postProcessor) throws Exception {
        mockMvc.perform(get("/public-api/images/sets/{id}", TEST_SET_ID)
                        .with(postProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].setId").value(TEST_SET_ID.toString()))
                .andExpect(jsonPath("$[0].localization").value(Localization.USA.getCode()))
                .andExpect(jsonPath("$[0].url").value("http://localhost:8080/images/test/url/image.png"));
    }

    @Test
    void save_should_saveSetImage_when_userHasRoles() throws Exception {
        mockMvc.perform(post("/api/images/sets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SAVE_SET_CONTENT)
                        .with(jwtWithRoles(SetImageRestController.WRITE_ROLE)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/images/sets/{id}", TEST_SET_ID)
                        .with(jwtWithRoles(SetImageRestController.READ_ROLE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].setId").value(TEST_SET_ID.toString()))
                .andExpect(jsonPath("$[1].localization").value(Localization.FRANCE.getCode()))
                .andExpect(jsonPath("$[1].imageId").value(ImageRestControllerIntegrationTests.TEST_IMAGE_ID.toString()));
    }

    @ParameterizedTest
    @AnonymousRequestSource
    @JwtRequestSource
    @JwtRequestSource(SetImageRestController.READ_ROLE)
    void save_should_beForbidden(RequestPostProcessor postProcessor) throws Exception {
        mockMvc.perform(post("/api/images/sets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SAVE_SET_CONTENT)
                        .with(postProcessor))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_should_deleteSetImage_when_userHasRoles() throws Exception {
        mockMvc.perform(delete("/api/images/sets/{id}/{localization}", TEST_SET_ID_2, Localization.USA.getCode())
                        .with(jwtWithRoles(SetImageRestController.WRITE_ROLE)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/images/sets/{id}", TEST_SET_ID_2)
                        .with(jwtWithRoles(SetImageRestController.READ_ROLE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @ParameterizedTest
    @AnonymousRequestSource
    @JwtRequestSource
    @JwtRequestSource(SetImageRestController.READ_ROLE)
    void delete_should_beForbidden(RequestPostProcessor postProcessor) throws Exception {
        mockMvc.perform(delete("/api/images/sets/{id}/{localization}", TEST_SET_ID_2, Localization.USA.getCode())
                        .with(postProcessor))
                .andExpect(status().isForbidden());
    }
}
