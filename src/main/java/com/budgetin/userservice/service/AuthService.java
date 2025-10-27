package com.budgetin.userservice.service;

import com.budgetin.userservice.dto.request.RegisterRequest;
import com.budgetin.userservice.dto.response.RegisterResponse;
import com.budgetin.userservice.exception.RegistrationException;
import com.budgetin.userservice.model.User;
import com.budgetin.userservice.model.UserSecurity;
import com.budgetin.userservice.model.UserStatus; // Pastikan ini di-import
import com.budgetin.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Dari Spring Security
    private final RedisOtpService redisOtpService; // Layanan koneksi ke Upstash

    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RedisOtpService redisOtpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisOtpService = redisOtpService;
    }

    /**
     * Menerima permintaan registrasi dan membuat user baru dengan status PENDING_VERIFICATION.
     * @param request Data registrasi dari user.
     * @return RegisterResponse dengan token dan ID user.
     * @throws RegistrationException Jika nomor HP atau email sudah terdaftar atau OTP tidak valid.
     */
    @Transactional
    public RegisterResponse registerUser(RegisterRequest request) {

        // 1. Validasi OTP (Pastikan user sudah memverifikasi nomor HP/email)
        // Kita gunakan OTP yang disimpan saat /send-otp
        String storedOtp = redisOtpService.getOtp("otp:" + request.getPhoneNumber());
        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {
            throw new RegistrationException("OTP tidak valid atau sudah kadaluarsa.");
        }
        // TODO: Tambahkan validasi token sementara dari /verify-otp jika diperlukan

        // 2. Cek Duplikasi (Nomor HP & Email harus unik)
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RegistrationException("Nomor HP sudah terdaftar.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RegistrationException("Email sudah terdaftar.");
        }

        // 3. Buat Entitas User Utama
        User user = new User();
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setStatus(UserStatus.PENDING_VERIFICATION); // Status awal
        // Default nama display, bisa diganti nanti
        user.setDisplayName("BudgetInUser" + ThreadLocalRandom.current().nextInt(1000, 9999));

        // 4. Buat Entitas Security
        UserSecurity security = new UserSecurity();
        security.setUser(user);

        // 5. Hashing Password & PIN (Wajib! Jangan simpan plain text)
        security.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        security.setPinHash(passwordEncoder.encode(request.getPin()));

        // 6. Set relasi dan simpan ke database (JPA akan menyimpan kedua tabel secara bersamaan)
        user.setUserSecurity(security);
        User savedUser = userRepository.save(user);

        // 7. Bersihkan OTP dari cache Upstash setelah sukses register
        redisOtpService.deleteOtp("otp:" + request.getPhoneNumber());
        // TODO: Bersihkan juga token sementara jika ada

        // 8. Generate Access Token (Simulasi, di Production pakai JWT/OAuth)
        String accessToken = generateMockAccessToken(savedUser.getId());

        return new RegisterResponse(savedUser.getId(), savedUser.getStatus().name(), accessToken);
    }

    // Metode simulasi untuk pembuatan token
    private String generateMockAccessToken(UUID userId) {
        // Di sini seharusnya ada logika JWT, tapi untuk proposal kita pakai UUID + base64 encode
        return java.util.Base64.getEncoder().encodeToString(userId.toString().getBytes()) + ".MOCK_TOKEN";
    }

    /**
     * Mengirim OTP ke nomor HP (Simulasi, di Production pakai layanan SMS/WA).
     * @param phoneNumber Nomor HP tujuan.
     * @return Kode OTP yang dikirim.
     */
    public String sendOtp(String phoneNumber) {
        // Cek apakah nomor sudah terdaftar (jika sudah, jangan kirim OTP, suruh login)
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new RegistrationException("Nomor HP sudah terdaftar. Silakan login.");
        }

        // Generate 6 digit kode acak
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));

        // Simpan ke Redis (default expire 5 menit / 300 detik)
        // PERBAIKAN DI SINI: Argumen ke-3 (waktu expire) DIHAPUS
        redisOtpService.setOtp("otp:" + phoneNumber, otp);

        // SIMULASI: Di production, di sini akan ada panggilan ke layanan SMS/WA (misal: Twilio, WA Business API)
        System.out.println("--- SIMULASI SMS/WA ---");
        System.out.println("OTP untuk " + phoneNumber + ": " + otp);
        System.out.println("-----------------------");

        return otp; // Mengembalikan OTP untuk tujuan testing/debugging saja
    }

    /**
     * Memverifikasi OTP.
     * @param phoneNumber Nomor HP
     * @param otp Kode OTP
     * @return String berupa token verifikasi sementara jika OTP valid, null jika tidak.
     */
    public String verifyOtp(String phoneNumber, String otp) {
        String storedOtp = redisOtpService.getOtp("otp:" + phoneNumber); // Ambil OTP dari Redis

        if (storedOtp != null && storedOtp.equals(otp)) {
            // Beri token verifikasi jangka pendek (misal: 10 menit / 600 detik)
            // Token ini digunakan oleh klien sebagai bukti bahwa OTP valid saat memanggil /register
            String tempToken = java.util.Base64.getEncoder().encodeToString(phoneNumber.getBytes());

            // Simpan token sementara ke Redis (default expire 5 menit / 300 detik)
            // PERBAIKAN DI SINI: Argumen ke-3 (waktu expire) DIHAPUS
            redisOtpService.setOtp("verify:" + phoneNumber, tempToken);

            // Kita TIDAK menghapus OTP di sini. OTP akan dihapus setelah /register sukses.
            return tempToken; // Kembalikan token sementara ke klien
        }
        return null; // OTP tidak cocok atau tidak ada
    }
}

