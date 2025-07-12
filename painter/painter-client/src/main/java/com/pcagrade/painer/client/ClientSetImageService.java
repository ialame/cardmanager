package com.pcagrade.painer.client;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.commons.FluxHelper;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.web.client.IWebClientConfigurer;
import com.pcagrade.painter.common.image.set.ISetImageService;
import com.pcagrade.painter.common.image.set.PublicSetImageDTO;
import com.pcagrade.painter.common.image.set.SetImageDTO;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class ClientSetImageService implements ISetImageService {

    private final WebClient webClient;

    public ClientSetImageService(@Nonnull PainterClientProperties properties, IWebClientConfigurer webClientConfigurer) {
        this.webClient = WebClient.builder()
                .baseUrl(properties.baseUrl() + "/api/images/sets")
                .apply(webClientConfigurer.json()
                        .oauth2(properties.oauth2RegistrationId())
                        .build())
                .build();
    }

    @Nonnull
    @Override
    public List<SetImageDTO> findAllBySetId(@Nullable Ulid setId) {
        return FluxHelper.blockList(webClient.get()
                .uri("/api/images/sets/{setId}", setId)
                .retrieve()
                .bodyToFlux(SetImageDTO.class));
    }

    @Nonnull
    @Override
    public List<PublicSetImageDTO> findAllPublicBySetId(@Nullable Ulid setId) {
        return FluxHelper.blockList(webClient.get()
                .uri("/public-api/images/sets/{setId}/", setId)
                .retrieve()
                .bodyToFlux(PublicSetImageDTO.class));
    }

    @Override
    public void saveSetImage(@Nonnull SetImageDTO dto) {
        saveSetImages(List.of(dto));
    }

    @Override
    public void saveSetImages(@Nonnull Iterable<SetImageDTO> dto) {
        webClient.post()
                .uri("/api/images/sets")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public void deleteSetImage(@Nonnull Ulid setId, @Nonnull Localization localization) {
        webClient.delete()
                .uri("/api/images/sets/{setId}/{localization}", setId, localization)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

    }
}
