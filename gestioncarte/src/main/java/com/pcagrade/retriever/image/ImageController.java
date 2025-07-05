package com.pcagrade.retriever.image;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Base64;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    private final Path root = Paths.get("uploads"); // Configurez le dossier d'upload ici

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExtractedImageDTO> createImage(
            @RequestParam("localization") Localization localization,
            @RequestParam("source") String source,
            @RequestParam("url") String url,
            @RequestParam("internal") boolean internal,
            @RequestParam("base64Image") String base64Image

    ) {
        try {
            // Vérifier que le dossier existe sinon le créer
            Path folderPath = root.resolve(url);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            // Générer un id unique pour l'image
            Ulid id = Ulid.fast();
            // Générer le chemin complet du fichier
            Path filePath = folderPath.resolve(id.toString() + ".jpg");

            // Sauvegarder le fichier
            //Files.copy(base64Image.getInputStream(), filePath);

            // Sauvegarder les infos dans la base
            Image image = new Image();
            image.setId(id);
            image.setPath(url + "/" + id.toString() + ".jpg");
            image.setSource(source);
            image.setInternal(internal);
            //image.setLocalization(localization);
            imageRepository.save(image);

            // Construire le DTO de retour
            ExtractedImageDTO extractedImageDTO = new ExtractedImageDTO(
                    localization,
                    source,
                    "/api/images/" + id,
                    internal,
                    getBase64Image(filePath)
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(extractedImageDTO);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error saving image", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable Ulid id) {
        Optional<Image> imageOptional = imageRepository.findById(id);

        if (imageOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Image image = imageOptional.get();
        String fileSystemPath = root.resolve(image.getPath()).toString();
        File imageFile = new File(fileSystemPath);

        if (!imageFile.exists() || !imageFile.isFile()) {
            return ResponseEntity.notFound().build();
        }

        Resource imageResource = new FileSystemResource(imageFile);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageFile.getName() + "\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageResource);
    }
    private String getBase64Image(Path filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(filePath);
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
