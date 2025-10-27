package com.budgetin.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_kyc_data")
@Data
@NoArgsConstructor
public class UserKycData {

    @Id
    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String nik;

    @Column(name = "ktp_full_name")
    private String ktpFullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String gender;
    
    @Column(name = "ktp_address")
    private String ktpAddress;

    @Column(name = "domicile_address")
    private String domicileAddress;

    private String occupation;
    
    @Column(name = "source_of_funds")
    private String sourceOfFunds;

    @Column(name = "account_opening_purpose")
    private String accountOpeningPurpose;

    @Column(name = "ktp_image_url", nullable = false)
    private String ktpImageUrl;

    @Column(name = "liveness_image_url", nullable = false)
    private String livenessImageUrl;

// JADINYA SEPERTI INI:
    @Enumerated(EnumType.STRING)
    @Column(name = "dukcapil_validation_status")
    private DukcapilStatus dukcapilValidationStatus;

    @CreationTimestamp
    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "verified_at")
    private OffsetDateTime verifiedAt;

    // Relasi 1-to-1
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}

