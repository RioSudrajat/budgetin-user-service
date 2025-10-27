package com.budgetin.userservice.exception;

/**
 * Exception kustom untuk error registrasi (duplikasi, OTP salah, dll.).
 */
public class RegistrationException extends RuntimeException {
    public RegistrationException(String message) {
        super(message);
    }
}
