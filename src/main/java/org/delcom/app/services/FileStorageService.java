package org.delcom.app.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    @Value("${app.upload.dir:./uploads}")
    protected String uploadDir;

    // Method lama untuk Todo (tetap dipakai untuk backward compatibility)
    public String storeFile(MultipartFile file, UUID todoId) throws IOException {
        // Buat directory jika belum ada
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = "cover_" + todoId.toString() + fileExtension;

        // Simpan file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    // Method baru untuk Laundry (tanpa UUID parameter)
    public String store(MultipartFile file) throws IOException {
        // Buat directory jika belum ada
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename dengan timestamp
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = "laundry_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + fileExtension;

        // Simpan file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    public boolean deleteFile(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }

    public Path loadFile(String filename) {
        return Paths.get(uploadDir).resolve(filename);
    }

    // Method baru untuk load sebagai Resource
    // MODIFIED: Gunakan protected method yang bisa di-override untuk testing
    public Resource loadAsResource(String filename) throws IOException {
        try {
            Path filePath = loadFile(filename);
            URI fileUri = convertPathToUri(filePath);
            Resource resource = new UrlResource(fileUri.toURL());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("File tidak ditemukan atau tidak dapat dibaca: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new IOException("File tidak ditemukan: " + filename, e);
        }
    }

    // Protected method untuk testing
    protected URI convertPathToUri(Path filePath) {
        return filePath.toUri();
    }

    public boolean fileExists(String filename) {
        return Files.exists(loadFile(filename));
    }
}