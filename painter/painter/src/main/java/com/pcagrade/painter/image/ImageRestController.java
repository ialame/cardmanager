package com.pcagrade.painter.image;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.painter.common.image.IImageService;
import com.pcagrade.painter.common.image.ImageDTO;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/images/")
public class ImageRestController {

    public static final String READ_ROLE = "painter.pcagrade.com/images.read";
    public static final String WRITE_ROLE = "painter.pcagrade.com/images.write";

    private final IImageService imageService;
    private final TimeLimiter timeLimiter = TimeLimiter.of(TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofMinutes(1))
            .build());

    public ImageRestController(IImageService imageService) {
        this.imageService = imageService;
    }

    @RolesAllowed(READ_ROLE)
    @GetMapping("{id}")
    public Optional<ImageDTO> get(@PathVariable("id") Ulid id) {
        return imageService.findById(id);
    }

    @RolesAllowed(WRITE_ROLE)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Callable<ImageDTO> upload(@RequestParam("file") MultipartFile file, @RequestParam("path") String path, @RequestParam(value = "source", required = false) String source, @RequestParam(value = "internal", required = false, defaultValue = "false") boolean internal) {
        return timeLimiter.decorateFutureSupplier(() -> CompletableFuture.supplyAsync(() -> {
            try {
                return imageService.create(path, source, internal, file.getBytes());
            } catch (IOException e) {
                throw new ImageUploadException(e);
            }
        }));
    }
}
