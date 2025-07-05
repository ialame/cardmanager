package com.pcagrade.painer.client;

import com.pcagrade.mason.web.client.IWebClientConfigurer;
import com.pcagrade.painter.common.image.ImageDTO;
import com.pcagrade.painter.common.image.legacy.ILegacyImageService;
import com.pcagrade.painter.common.image.legacy.RestoreLegacyImageRequest;
import jakarta.annotation.Nonnull;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Optional;

public class ClientLegacyImageService implements ILegacyImageService {

    private final WebClient webClient;

    public ClientLegacyImageService(@Nonnull PainterClientProperties properties, IWebClientConfigurer webClientConfigurer) {
        this.webClient = WebClient.builder()
                .baseUrl(properties.baseUrl() + "/api/images/legacy/")
                .apply(webClientConfigurer.json()
                        .oauth2(properties.oauth2RegistrationId())
                        .build())
                .build();
    }

    @Override
    public String findById(String legacyId) throws IOException {
        return webClient.get()
                .uri(builder -> builder.path("/{id}").build(legacyId))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public Optional<ImageDTO> restoreImage(String folder, String path, boolean internal) {
        return webClient.post()
                .bodyValue(new RestoreLegacyImageRequest(folder, path, internal))
                .retrieve()
                .bodyToMono(ImageDTO.class)
                .blockOptional();
    }
}
