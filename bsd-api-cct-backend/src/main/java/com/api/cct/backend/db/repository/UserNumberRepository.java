package com.api.cct.backend.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.cct.backend.db.entity.UserNumber;

public interface UserNumberRepository extends JpaRepository<UserNumber, String> {
}
