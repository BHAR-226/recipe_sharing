package com.bhar.recipe_sharing.image;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bhar.recipe_sharing.exception.BadRequestException;
import com.bhar.recipe_sharing.exception.NotFoundException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.annotation.PostConstruct;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "webp", "gif", "svg"
    );

    @Value("${app.upload.dir:uploads/images}")
    private String configuredUploadDir;

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    private Cloudinary cloudinary;
    private Path rootLocation;

    @PostConstruct
    public void init() {
        if (isCloudinaryConfigured()) {
            this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName.trim(),
                "api_key", apiKey.trim(),
                "api_secret", apiSecret.trim(),
                "secure", true
            ));
            log.info("Cloudinary storage initialized successfully for cloud: {}", cloudName.trim());
        } else {
            log.info("Cloudinary credentials not provided. Using local disk storage.");
        }

        Path directPath = Paths.get(configuredUploadDir).toAbsolutePath().normalize();
        Path backendPrefixed = Paths.get("backend", configuredUploadDir).toAbsolutePath().normalize();

        if (!Files.exists(directPath) && Files.exists(backendPrefixed.getParent())) {
            this.rootLocation = backendPrefixed;
        } else {
            this.rootLocation = directPath;
        }

        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    public boolean isCloudinaryConfigured() {
        return cloudName != null && !cloudName.isBlank()
            && apiKey != null && !apiKey.isBlank()
            && apiSecret != null && !apiSecret.isBlank();
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Impossible d'enregistrer un fichier vide.");
        }

        String originalFilename = StringUtils.cleanPath(
            file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg"
        );

        String extension = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT))) {
            throw new BadRequestException("Format d'image non supporté. Formats acceptés : JPG, PNG, WEBP, GIF, SVG");
        }

        // If Cloudinary is configured, upload to Cloudinary
        if (isCloudinaryConfigured()) {
            try {
                Map<?, ?> uploadResult = this.cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                        "folder", "recipenet",
                        "resource_type", "image"
                    )
                );
                String secureUrl = (String) uploadResult.get("secure_url");
                if (secureUrl != null && !secureUrl.isBlank()) {
                    log.info("Uploaded image to Cloudinary: {}", secureUrl);
                    return secureUrl;
                }
            } catch (Exception e) {
                log.error("Failed to upload to Cloudinary, falling back to local disk storage", e);
            }
        }

        // Fallback to local disk storage
        String safeFileName = UUID.randomUUID() + "." + extension.toLowerCase(Locale.ROOT);
        Path destinationFile = this.rootLocation.resolve(safeFileName).normalize().toAbsolutePath();

        if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
            throw new BadRequestException("Chemin de fichier invalide.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Échec de la sauvegarde de l'image.", e);
        }

        return safeFileName;
    }

    public Resource loadAsResource(String filename) {
        String cleanFilename = Paths.get(filename).getFileName().toString();
        List<Path> candidateDirs = List.of(
            this.rootLocation,
            Paths.get("uploads", "images").toAbsolutePath().normalize(),
            Paths.get("backend", "uploads", "images").toAbsolutePath().normalize(),
            Paths.get("..", "uploads", "images").toAbsolutePath().normalize()
        );

        for (Path dir : candidateDirs) {
            try {
                Path file = dir.resolve(cleanFilename).normalize().toAbsolutePath();
                Resource resource = new UrlResource(file.toUri());
                if (resource.exists() && resource.isReadable()) {
                    return resource;
                }
            } catch (MalformedURLException ignored) {
            }
        }

        throw new NotFoundException("Fichier introuvable ou inaccessible : " + filename);
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "jpg";
    }
}
