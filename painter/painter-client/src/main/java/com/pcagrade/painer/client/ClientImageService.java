package com.pcagrade.painer.client;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.web.client.IWebClientConfigurer;
import com.pcagrade.painter.common.image.IImageService;
import com.pcagrade.painter.common.image.ImageDTO;
import com.pcagrade.painter.common.image.ImageHelper;
import jakarta.annotation.Nonnull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

public class ClientImageService implements IImageService {

    private final WebClient webClient;

    public ClientImageService(@Nonnull PainterClientProperties properties, IWebClientConfigurer webClientConfigurer) {
        this.webClient = WebClient.builder()
                .baseUrl(properties.baseUrl() + "/api/images/")
                .apply(webClientConfigurer.json()
                        .oauth2(properties.oauth2RegistrationId())
                        .build())
                .build();
    }

    @Override
    public Optional<ImageDTO> findById(Ulid id) {
        if (id == null) {
            return Optional.empty();
        }

        return webClient.get()
                .uri(builder -> builder.path("/{id}").build(id))
                .retrieve()
                .bodyToMono(ImageDTO.class)
                .blockOptional();
    }

    @Override
    public ImageDTO create(String folder, String source, boolean internal, @Nonnull byte[] sourceImage) {
        var builder = new MultipartBodyBuilder();

        builder.part("file", new ByteArrayResource(sourceImage)).filename("image.png");
        builder.part("path", folder);
        builder.part("source", source);
        builder.part("internal", internal);

        return webClient.post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(ImageDTO.class)
                .block();
    }

    @Override
    public ImageDTO create(String folder, String source, boolean internal, @Nonnull BufferedImage sourceImage) throws IOException {
        return create(folder, source, internal, ImageHelper.toByteArray(sourceImage, "png"));
    }
}
