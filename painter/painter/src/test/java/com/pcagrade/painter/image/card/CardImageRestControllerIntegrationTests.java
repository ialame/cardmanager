package com.pcagrade.painter.image.card;

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
class CardImageRestControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    private static final Ulid TEST_CARD_IMAGE_ID = Ulid.from("01HKMMHDQZHP7SNZCNW5QDA740");
    private static final Ulid TEST_CARD_ID = Ulid.from("01HSB2TSHWGBMEW0VRJQ2HEC6X");
    private static final Ulid TEST_CARD_ID_2 = Ulid.from("01HSDEYAAAPRKHPHVWWH6MYW7G");
    private static final String SAVE_CARD_CONTENT = "[{\"cardId\":\"" + TEST_CARD_ID + "\",\"localization\":\"fr\",\"imageId\":\"" + ImageRestControllerIntegrationTests.TEST_IMAGE_ID + "\"}]";

    @ParameterizedTest
    @AnonymousRequestSource
    @JwtRequestSources(@JwtRequestSource)
    void findAllByCardId_should_beForbidden(RequestPostProcessor postProcessor) throws Exception {
        mockMvc.perform(get("/api/images/cards/{id}", TEST_CARD_ID)
                        .with(postProcessor))
                .andExpect(status().isForbidden());
    }

    @Test
    void findAllByCardId_should_returnCardImage_when_userHasRoles() throws Exception {
        mockMvc.perform(get("/api/images/cards/{id}", TEST_CARD_ID)
                        .with(jwtWithRoles(CardImageRestController.READ_ROLE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cardId").value(TEST_CARD_ID.toString()))
                .andExpect(jsonPath("$[0].localization").value(Localization.USA.getCode()))
                .andExpect(jsonPath("$[0].imageId").value(ImageRestControllerIntegrationTests.TEST_IMAGE_ID.toString()));
    }

    @ParameterizedTest
    @AnonymousRequestSource
    @JwtRequestSource
    @JwtRequestSource(CardImageRestController.READ_ROLE)
    void findAllPublicByCardId_should_returnPublicCardImage(RequestPostProcessor postProcessor) throws Exception {
        mockMvc.perform(get("/public-api/images/cards/{id}", TEST_CARD_ID)
                        .with(postProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cardId").value(TEST_CARD_ID.toString()))
                .andExpect(jsonPath("$[0].localization").value(Localization.USA.getCode()))
                .andExpect(jsonPath("$[0].url").value("http://localhost:8080/images/test/url/image.png"));
    }

    @Test
    void save_should_saveCardImage_when_userHasRoles() throws Exception {
        mockMvc.perform(post("/api/images/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SAVE_CARD_CONTENT)
                        .with(jwtWithRoles(CardImageRestController.WRITE_ROLE)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/images/cards/{id}", TEST_CARD_ID)
                        .with(jwtWithRoles(CardImageRestController.READ_ROLE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].cardId").value(TEST_CARD_ID.toString()))
                .andExpect(jsonPath("$[1].localization").value(Localization.FRANCE.getCode()))
                .andExpect(jsonPath("$[1].imageId").value(ImageRestControllerIntegrationTests.TEST_IMAGE_ID.toString()));
    }

    @ParameterizedTest
    @AnonymousRequestSource
    @JwtRequestSource
    @JwtRequestSource(CardImageRestController.READ_ROLE)
    void save_should_beForbidden(RequestPostProcessor postProcessor) throws Exception {
        mockMvc.perform(post("/api/images/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SAVE_CARD_CONTENT)
                        .with(postProcessor))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_should_deleteCardImage_when_userHasRoles() throws Exception {
        mockMvc.perform(delete("/api/images/cards/{id}/{localization}", TEST_CARD_ID_2, Localization.USA.getCode())
                        .with(jwtWithRoles(CardImageRestController.WRITE_ROLE)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/images/cards/{id}", TEST_CARD_ID_2)
                        .with(jwtWithRoles(CardImageRestController.READ_ROLE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @ParameterizedTest
    @AnonymousRequestSource
    @JwtRequestSource
    @JwtRequestSource(CardImageRestController.READ_ROLE)
    void delete_should_beForbidden(RequestPostProcessor postProcessor) throws Exception {
        mockMvc.perform(delete("/api/images/cards/{id}/{localization}", TEST_CARD_ID_2, Localization.USA.getCode())
                        .with(postProcessor))
                .andExpect(status().isForbidden());
    }
}
