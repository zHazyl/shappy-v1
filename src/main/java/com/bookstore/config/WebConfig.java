package com.bookstore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/images/books}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded images from the runtime upload directory
        // This maps /uploads/images/books/** URLs to the actual upload directory
        File uploadDirectory = new File(uploadDir);
        String uploadPath = "file:" + uploadDirectory.getAbsolutePath() + "/";
        
        registry.addResourceHandler("/uploads/images/books/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(3600); // Cache for 1 hour
        
        System.out.println("Configured image serving from: " + uploadPath);
        
        // Spring Boot automatically serves static content from:
        // - src/main/resources/static/
        // - src/main/resources/public/
        // - src/main/resources/resources/
        // - src/main/resources/META-INF/resources/
        //
        // Static images in src/main/resources/static/images/ are accessible at:
        // http://localhost:8080/images/
        //
        // Uploaded images are now accessible at:
        // http://localhost:8080/uploads/images/books/
    }
} 