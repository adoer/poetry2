package com.poetry.config;

import com.poetry.entity.User;
import com.poetry.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isPresent()) {
            log.info("Admin user already exists, skipping initialization");
            return;
        }

        String password = System.getenv("ADMIN_PASSWORD");
        if (password == null || password.isBlank()) {
            password = "admin123";
            log.warn("ADMIN_PASSWORD env var not set, using default password");
        }

        User admin = new User();
        admin.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode(password));
        admin.setVipLevel("admin");
        userRepository.save(admin);

        log.info("Default admin user created successfully");
    }
}
