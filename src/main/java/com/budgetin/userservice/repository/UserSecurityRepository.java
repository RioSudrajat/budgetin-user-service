package com.budgetin.userservice.repository;

import com.budgetin.userservice.model.UserSecurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserSecurityRepository extends JpaRepository<UserSecurity, UUID> {
    // Fungsi standar (save, findById, etc.) sudah cukup
}
