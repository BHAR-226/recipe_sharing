package com.bhar.recipe_sharing.image;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bhar.recipe_sharing.image.dto.ImageUploadResponse;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final FileStorageService storageService;

    public ImageController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ImageUploadResponse> uploadSingle(@RequestParam("file") MultipartFile file) {
        String stored = storageService.store(file);
        String url = (stored.startsWith("http://") || stored.startsWith("https://")) ? stored : "/api/images/" + stored;
        return ResponseEntity.status(HttpStatus.CREATED).body(new ImageUploadResponse(url, stored));
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<List<ImageUploadResponse>> uploadMultiple(@RequestParam("files") List<MultipartFile> files) {
        List<ImageUploadResponse> responses = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String stored = storageService.store(file);
                    String url = (stored.startsWith("http://") || stored.startsWith("https://")) ? stored : "/api/images/" + stored;
                    responses.add(new ImageUploadResponse(url, stored));
                }
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);

        String contentType = null;
        try {
            contentType = Files.probeContentType(file.getFile().toPath());
        } catch (IOException ignored) {
        }

        if (contentType == null) {
            if (filename.toLowerCase().endsWith(".png")) contentType = "image/png";
            else if (filename.toLowerCase().endsWith(".webp")) contentType = "image/webp";
            else if (filename.toLowerCase().endsWith(".gif")) contentType = "image/gif";
            else if (filename.toLowerCase().endsWith(".svg")) contentType = "image/svg+xml";
            else contentType = "image/jpeg";
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
            .body(file);
    }
}

