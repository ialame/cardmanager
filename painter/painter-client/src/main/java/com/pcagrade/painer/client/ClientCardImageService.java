package com.pcagrade.painer.client;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.commons.FluxHelper;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.web.client.IWebClientConfigurer;
import com.pcagrade.painter.common.image.card.CardImageDTO;
import com.pcagrade.painter.common.image.card.ICardImageService;
import com.pcagrade.painter.common.image.card.PublicCardImageDTO;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class ClientCardImageService implements ICardImageService {

    private final WebClient webClient;


    public ClientCardImageService(@Nonnull PainterClientProperties properties, IWebClientConfigurer webClientConfigurer) {
        this.webClient = WebClient.builder()
                .baseUrl(properties.baseUrl())
                .apply(webClientConfigurer.json()
                        .oauth2(properties.oauth2RegistrationId())
                        .build())
                .build();
    }

    @Nonnull
    @Override
    public List<CardImageDTO> findAllByCardId(@Nullable Ulid cardId) {
        return FluxHelper.blockList(webClient.get()
                .uri("/api/images/cards/{cardId}", cardId)
                .retrieve()
                .bodyToFlux(CardImageDTO.class));
    }

    @Nonnull
    @Override
    public List<PublicCardImageDTO> findAllPublicByCardId(@Nullable Ulid cardId) {
        return FluxHelper.blockList(webClient.get()
                .uri("/public-api/images/cards/{cardId}/public", cardId)
                .retrieve()
                .bodyToFlux(PublicCardImageDTO.class));
    }

    @Override
    public void saveCardImage(@Nonnull CardImageDTO dto) {
        saveCardImages(List.of(dto));
    }

    @Override
    public void saveCardImages(@Nonnull Iterable<CardImageDTO> dto) {
        webClient.post()
                .uri("/api/images/cards")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public void deleteCardImage(@Nonnull Ulid cardId, @Nonnull Localization localization) {
        webClient.delete()
                .uri("/api/images/cards/{cardId}/{localization}", cardId, localization)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

    }
}
