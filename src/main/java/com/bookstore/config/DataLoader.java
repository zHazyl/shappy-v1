package com.bookstore.config;

import com.bookstore.enums.Role;
import com.bookstore.models.User;
import com.bookstore.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        createDemoUsers();
    }

    private void createDemoUsers() {
        try {
            // Create admin user if doesn't exist
            if (!userService.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .password("admin123")
                        .email("admin@bookstore.com")
                        .role(Role.ADMIN)
                        .build();
                
                userService.registerUser(admin);
                logger.info("✅ Created demo admin user: admin / admin123");
            } else {
                logger.info("📋 Admin user already exists");
            }

            // Create customer user if doesn't exist
            if (!userService.existsByUsername("customer")) {
                User customer = User.builder()
                        .username("customer")
                        .password("cust123")
                        .email("customer@bookstore.com")
                        .role(Role.USER)
                        .build();
                
                userService.registerUser(customer);
                logger.info("✅ Created demo customer user: customer / cust123");
            } else {
                logger.info("📋 Customer user already exists");
            }

        } catch (Exception e) {
            logger.error("❌ Error creating demo users: {}", e.getMessage(), e);
        }
    }
} 