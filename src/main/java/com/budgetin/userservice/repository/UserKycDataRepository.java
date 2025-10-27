package com.budgetin.userservice.repository;

import com.budgetin.userservice.model.UserKycData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserKycDataRepository extends JpaRepository<UserKycData, UUID> {

    // SELECT * FROM user_kyc_data WHERE nik = ?
    Optional<UserKycData> findByNik(String nik);

    // SELECT (1) FROM user_kyc_data WHERE nik = ?
    boolean existsByNik(String nik);
}
