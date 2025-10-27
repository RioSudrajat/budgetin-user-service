package com.budgetin.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO untuk permintaan registrasi.
 * Mengandung Validasi Wajib (dari spring-boot-starter-validation)
 */
@Data
public class RegisterRequest {

    // Nomor HP harus angka, format internasional/lokal, tidak boleh kosong
    @NotBlank(message = "Nomor HP wajib diisi.")
    @Pattern(regexp = "^[0-9]{8,15}$", message = "Nomor HP tidak valid.")
    private String phoneNumber;

    // Email harus format email
    @NotBlank(message = "Email wajib diisi.")
    @Email(message = "Format email tidak valid.")
    private String email;

    // Password harus minimal 8 karakter
    @NotBlank(message = "Password wajib diisi.")
    @Size(min = 8, message = "Password minimal 8 karakter.")
    private String password;

    // PIN harus 6 digit angka
    @NotBlank(message = "PIN wajib diisi.")
    @Pattern(regexp = "^[0-9]{6}$", message = "PIN harus 6 digit angka.")
    private String pin;
    
    // Kode OTP yang dikirimkan user untuk verifikasi
    @NotBlank(message = "Kode OTP wajib diisi.")
    @Size(min = 6, max = 6, message = "OTP harus 6 digit.")
    private String otp;
    
    // Token verifikasi sementara dari /verify-otp (sebelumnya disimpan di Redis)
    @NotBlank(message = "Token verifikasi wajib diisi.")
    private String verificationToken; 
}
