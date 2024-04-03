package com.api.cct.backend.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.cct.backend.db.entity.UserDanswer;

public interface UserDanswerRepository extends JpaRepository<UserDanswer, String> {
}
