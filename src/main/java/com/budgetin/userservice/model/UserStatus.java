package com.budgetin.userservice.model;

/**
 * Enum ini harus SAMA PERSIS dengan ENUM 'user_status' di PostgreSQL.
 * Enum 'user_status' (PENDING_VERIFICATION, KYC_SUBMITTED, ACTIVE, SUSPENDED, CLOSED)
 */
public enum UserStatus {
    PENDING_VERIFICATION,
    KYC_SUBMITTED,
    ACTIVE,
    SUSPENDED,
    CLOSED
}

