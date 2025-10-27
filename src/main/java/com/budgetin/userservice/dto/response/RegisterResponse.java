package com.budgetin.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO untuk respons sukses setelah registrasi.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private UUID userId;
    private String status;
    private String accessToken; // Token sesi untuk memanggil API KYC
}
