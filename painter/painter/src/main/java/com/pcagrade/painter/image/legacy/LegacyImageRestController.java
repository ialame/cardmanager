package com.pcagrade.painter.image.legacy;

import com.pcagrade.painter.common.image.ImageDTO;
import com.pcagrade.painter.common.image.legacy.ILegacyImageService;
import com.pcagrade.painter.common.image.legacy.RestoreLegacyImageRequest;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/images/legacy/")
public class LegacyImageRestController {

    public static final String READ_ROLE = "painter.pcagrade.com/images/legacy.read";
    public static final String WRITE_ROLE = "painter.pcagrade.com/images/legacy.write";

    private final ILegacyImageService legacyImageService;

    public LegacyImageRestController(ILegacyImageService legacyImageService) {
        this.legacyImageService = legacyImageService;
    }

    @RolesAllowed(READ_ROLE)
    @GetMapping("{id}")
    public String find(@PathVariable("id") String id) throws IOException {
        return legacyImageService.findById(id);
    }

    @RolesAllowed(WRITE_ROLE)
    @PostMapping
    public Optional<ImageDTO> restore(@RequestBody RestoreLegacyImageRequest request) {
        return legacyImageService.restoreImage(request.folder(), request.path(), request.internal());
    }
}
