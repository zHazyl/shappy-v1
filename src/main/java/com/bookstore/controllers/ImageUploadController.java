package com.bookstore.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/api/upload")
public class ImageUploadController {

    private static final String UPLOAD_DIR = "src/main/resources/static/images/books/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".webp"};

    @PostMapping("/image")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate file
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select a file to upload");
                return ResponseEntity.badRequest().body(response);
            }

            // Check file size
            if (file.getSize() > MAX_FILE_SIZE) {
                response.put("success", false);
                response.put("message", "File size must be less than 5MB");
                return ResponseEntity.badRequest().body(response);
            }

            // Check file extension
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !isValidImageFile(originalFilename)) {
                response.put("success", false);
                response.put("message", "Only JPG, JPEG, PNG, and WebP files are allowed");
                return ResponseEntity.badRequest().body(response);
            }

            // Generate unique filename
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = generateUniqueFilename() + fileExtension;

            // Create upload directory if it doesn't exist
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save file
            Path targetPath = Paths.get(UPLOAD_DIR + uniqueFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Return success response
            response.put("success", true);
            response.put("message", "Image uploaded successfully");
            response.put("filename", uniqueFilename);
            response.put("url", "/images/books/" + uniqueFilename);
            response.put("size", file.getSize());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/image/{filename}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteImage(@PathVariable String filename) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate filename
            if (!isValidFilename(filename)) {
                response.put("success", false);
                response.put("message", "Invalid filename");
                return ResponseEntity.badRequest().body(response);
            }

            // Delete file
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                response.put("success", true);
                response.put("message", "Image deleted successfully");
            } else {
                response.put("success", false);
                response.put("message", "Image not found");
            }

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to delete image: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/images")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> listImages() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File[] files = uploadDir.listFiles((dir, name) -> isValidImageFile(name));
            
            if (files != null) {
                Map<String, Map<String, Object>> imageInfo = new HashMap<>();
                
                for (File file : files) {
                    Map<String, Object> info = new HashMap<>();
                    info.put("size", file.length());
                    info.put("url", "/images/books/" + file.getName());
                    info.put("lastModified", file.lastModified());
                    imageInfo.put(file.getName(), info);
                }
                
                response.put("success", true);
                response.put("images", imageInfo);
                response.put("count", files.length);
            } else {
                response.put("success", true);
                response.put("images", new HashMap<>());
                response.put("count", 0);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to list images: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private boolean isValidImageFile(String filename) {
        if (filename == null) return false;
        
        String lowerCaseFilename = filename.toLowerCase();
        for (String extension : ALLOWED_EXTENSIONS) {
            if (lowerCaseFilename.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidFilename(String filename) {
        return filename != null && 
               filename.matches("^[a-zA-Z0-9._-]+$") && 
               filename.length() <= 255 &&
               isValidImageFile(filename);
    }

    private String generateUniqueFilename() {
        return "book_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
} 