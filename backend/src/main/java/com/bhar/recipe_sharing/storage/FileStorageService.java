package com.bhar.recipe_sharing.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@Service
public class FileStorageService {

    private static final Logger log =
        LoggerFactory.getLogger(FileStorageService.class);

    public static final String RECIPES = "recipes";
    public static final String PROFILES = "profiles";

    private static final String PUBLIC_PREFIX = "/uploads/";
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    private static final Map<String, String> ALLOWED_TYPES = Map.of(
        "image/jpeg", ".jpg",
        "image/png", ".png",
        "image/webp", ".webp",
        "image/gif", ".gif"
    );

    private final Path root;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    void init() {
        try {
            Files.createDirectories(root.resolve(RECIPES));
            Files.createDirectories(root.resolve(PROFILES));
        } catch (IOException ex) {
            throw new IllegalStateException(
                "Could not create upload directories under " + root, ex
            );
        }
    }

    public String store(MultipartFile file, String subdir) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException(
                "File exceeds the maximum allowed size of 5MB"
            );
        }

        String extension = ALLOWED_TYPES.get(file.getContentType());
        if (extension == null) {
            throw new InvalidFileException(
                "Unsupported image type. Allowed types: JPEG, PNG, WEBP, GIF"
            );
        }

        String filename = UUID.randomUUID() + extension;
        Path target = resolveSafe(subdir, filename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException(
                "Failed to store file " + filename, ex
            );
        }

        return PUBLIC_PREFIX + subdir + "/" + filename;
    }

    public void delete(String publicPath) {
        if (publicPath == null || !publicPath.startsWith(PUBLIC_PREFIX)) {
            return;
        }

        String relative = publicPath.substring(PUBLIC_PREFIX.length());
        Path target;
        try {
            target = resolveSafe(relative);
        } catch (InvalidFileException ex) {
            log.warn("Refusing to delete unsafe path: {}", publicPath);
            return;
        }

        try {
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            log.warn("Failed to delete file {}: {}", publicPath, ex.getMessage());
        }
    }

    private Path resolveSafe(String subdir, String filename) {
        return resolveSafe(subdir + "/" + filename);
    }

    private Path resolveSafe(String relative) {
        Path target = root.resolve(relative).normalize();
        if (!target.startsWith(root)) {
            throw new InvalidFileException("Invalid file path");
        }
        return target;
    }
}
