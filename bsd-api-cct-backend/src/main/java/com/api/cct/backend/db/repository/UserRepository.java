package com.api.cct.backend.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.cct.backend.db.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    // find by username
    Optional<User> findByUsername(String username);
}
