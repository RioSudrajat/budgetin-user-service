package com.budgetin.userservice.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurasi untuk menginisialisasi Cloudinary SDK menggunakan URL dari application.properties.
 */
@Configuration
public class CloudinaryConfig {

    // Ambil URL dari application.properties
    @Value("${cloudinary.url}")
    private String cloudinaryUrl;

    @Bean
    public Cloudinary cloudinary() {
        // Cloudinary SDK bisa diinisialisasi hanya dengan URL
        return new Cloudinary(cloudinaryUrl);
    }
}
