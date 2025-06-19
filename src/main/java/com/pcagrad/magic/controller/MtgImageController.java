package com.pcagrad.magic.controller;

import com.pcagrad.magic.dto.ApiResponse;
import com.pcagrad.magic.service.ImageDownloadService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 5. CONTROLLER IMAGES - MtgImageController.java
 * Pour la gestion des images
 */
@RestController
@RequestMapping("/api/mtg/images")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"})
public class MtgImageController {

    @Autowired
    private ImageDownloadService imageDownloadService;

    @PostMapping("/download/{setCode}")
    public ResponseEntity<ApiResponse<String>> downloadImagesForSet(@PathVariable String setCode) {
        // Télécharger images pour une extension
        return null;
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<Resource> getCardImage(@PathVariable UUID cardId) {
        // Servir l'image d'une carte
        return null;
    }
}
