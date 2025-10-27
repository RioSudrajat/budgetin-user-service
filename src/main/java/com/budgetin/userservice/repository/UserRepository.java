package com.budgetin.userservice.repository;

import com.budgetin.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Spring Data JPA akan otomatis membuat query untuk kita
    // berdasarkan nama method ini
    
    // SELECT * FROM users WHERE phone_number = ?
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    // SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // SELECT (1) FROM users WHERE phone_number = ?
    boolean existsByPhoneNumber(String phoneNumber);

    // SELECT (1) FROM users WHERE email = ?
    boolean existsByEmail(String email);
}
