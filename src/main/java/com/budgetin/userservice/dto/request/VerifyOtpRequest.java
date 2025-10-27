package com.budgetin.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @NotBlank(message = "Nomor HP wajib diisi.")
    @Pattern(regexp = "^[0-9]{8,15}$", message = "Nomor HP tidak valid.")
    private String phoneNumber;

    @NotBlank(message = "Kode OTP wajib diisi.")
    @Size(min = 6, max = 6, message = "OTP harus 6 digit.")
    private String otp;
}
