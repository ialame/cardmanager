package com.pcagrade.painter.image.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.painter.common.image.set.PublicSetImageDTO;
import com.pcagrade.painter.common.image.set.SetImageDTO;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class SetImageRestController {

    public static final String READ_ROLE = "painter.pcagrade.com/images/sets.read";
    public static final String WRITE_ROLE = "painter.pcagrade.com/images/sets.write";

    private final SetImageService setImageService;

    public SetImageRestController(SetImageService setImageService) {
        this.setImageService = setImageService;
    }

    @RolesAllowed(READ_ROLE)
    @GetMapping("/api/images/sets/{setId}")
    public List<SetImageDTO> findAllBySetId(@PathVariable("setId") Ulid setId) {
        return setImageService.findAllBySetId(setId);
    }

    @PermitAll
    @GetMapping("/public-api/images/sets/{setId}")
    public List<PublicSetImageDTO> findAllPublicBySetId(@PathVariable("setId") Ulid setId) {
        return setImageService.findAllPublicBySetId(setId);
    }

    @RolesAllowed(WRITE_ROLE)
    @PostMapping("/api/images/sets")
    public void saveSetImage(@RequestBody List<SetImageDTO> images) {
        setImageService.saveSetImages(images);
    }

    @RolesAllowed(WRITE_ROLE)
    @DeleteMapping("/api/images/sets/{setId}/{localization}")
    public void deleteSetImage(@PathVariable("setId") Ulid setId, @PathVariable("localization") Localization localization) {
        setImageService.deleteSetImage(setId, localization);
    }
}
