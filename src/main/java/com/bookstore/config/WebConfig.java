package com.bookstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Spring Boot automatically serves static content from:
    // - src/main/resources/static/
    // - src/main/resources/public/
    // - src/main/resources/resources/
    // - src/main/resources/META-INF/resources/
    //
    // Images in src/main/resources/static/images/ are accessible at:
    // http://localhost:8080/images/
    //
    // No custom ResourceHandler configuration needed!

} 