package com.budgetin.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Konfigurasi Spring Security.
 * Bertanggung jawab untuk:
 * 1. Mendefinisikan PasswordEncoder (BCrypt).
 * 2. Mengatur izin (permission) untuk setiap endpoint API.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Ini adalah @Bean yang akan di-inject ke AuthService untuk mengenkripsi password & PIN.
     * Kita menggunakan BCrypt, standar industri untuk hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Ini adalah @Bean yang mengatur "Firewall" aplikasi kita.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Nonaktifkan CSRF (wajib untuk API stateless)
            .csrf(csrf -> csrf.disable())
            
            // 2. Atur izin (Authorization Rules)
            .authorizeHttpRequests(authz -> authz
                // Izinkan semua orang mengakses endpoint onboarding (send-otp, verify, register)
                .requestMatchers("/api/v1/auth/**").permitAll() 
                // Izinkan semua orang mengakses endpoint health check (penting untuk Render)
                .requestMatchers("/health").permitAll()
                // Endpoint lain (seperti /api/v1/kyc/**) harus diautentikasi
                .anyRequest().authenticated() 
            )
            
            // 3. Atur Session Management
            // Kita pakai API stateless (tidak pakai session/cookie), 
            // nanti kita akan validasi pakai JWT (access token)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
