package com.budgetin.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SendOtpRequest {
    @NotBlank(message = "Nomor HP wajib diisi.")
    @Pattern(regexp = "^[0-9]{8,15}$", message = "Nomor HP tidak valid.")
    private String phoneNumber;
}
