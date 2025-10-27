package com.budgetin.userservice.controller;

import com.budgetin.userservice.dto.request.RegisterRequest;
import com.budgetin.userservice.dto.request.SendOtpRequest;
import com.budgetin.userservice.dto.request.VerifyOtpRequest;
import com.budgetin.userservice.dto.response.RegisterResponse;
import com.budgetin.userservice.exception.RegistrationException;
import com.budgetin.userservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map; // Import Map

@RestController
@RequestMapping("/api/v1/auth") // Base path untuk endpoint autentikasi
public class OnboardingController {

    private final AuthService authService;

    @Autowired
    public OnboardingController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint untuk mengirim OTP (simulasi).
     */
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody SendOtpRequest request) {
        try {
            String otp = authService.sendOtp(request.getPhoneNumber());
            // Mengembalikan OTP dalam respons hanya untuk memudahkan testing di proposal ini
            // Di production, JANGAN PERNAH kembalikan OTP dalam respons.
            return ResponseEntity.ok(Map.of(
                    "message", "OTP (simulasi) telah dikirim.",
                    "simulated_otp", otp
            ));
        } catch (RegistrationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
             // Tangkap error umum lainnya
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Terjadi kesalahan internal."));
        }
    }

    /**
     * Endpoint untuk memverifikasi OTP.
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody VerifyOtpRequest request) {
         try {
            // PERBAIKAN DI SINI:
            // authService.verifyOtp() sekarang mengembalikan String (token) atau null
            String verificationToken = authService.verifyOtp(request.getPhoneNumber(), request.getOtp());

            // Kita cek apakah tokennya TIDAK null
            if (verificationToken != null) {
                // Mengembalikan token verifikasi sementara ke klien
                // Klien harus menyertakan token ini saat memanggil /register
                return ResponseEntity.ok(Map.of(
                        "message", "OTP valid.",
                        "verificationToken", verificationToken // Kirim token ke klien
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "OTP tidak valid atau kadaluarsa."));
            }
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Terjadi kesalahan internal."));
        }
    }

    /**
     * Endpoint untuk registrasi user baru.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            // TODO: Tambahkan validasi untuk verificationToken dari /verify-otp di sini
            RegisterResponse response = authService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RegistrationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
             e.printStackTrace(); // Cetak stack trace untuk debugging
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Gagal melakukan registrasi."));
        }
    }

    // Tambahkan endpoint lain jika perlu (login, forgot password, dll.)
}

