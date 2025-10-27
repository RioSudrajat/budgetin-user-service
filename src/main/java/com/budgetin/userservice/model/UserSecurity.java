package com.budgetin.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_security")
@Data
@NoArgsConstructor
public class UserSecurity {

    @Id
    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "pin_hash", nullable = false)
    private String pinHash;

    @Column(name = "is_biometric_enabled", nullable = false)
    private boolean isBiometricEnabled = false;

    @Column(name = "last_login_at")
    private OffsetDateTime lastLoginAt;

    // Ini adalah kunci relasi 1-to-1
    @OneToOne
    @MapsId // Menandakan bahwa Primary Key (userId) juga merupakan Foreign Key
    @JoinColumn(name = "user_id")
    private User user;
}

