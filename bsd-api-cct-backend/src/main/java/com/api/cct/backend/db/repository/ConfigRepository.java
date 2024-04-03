package com.api.cct.backend.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.cct.backend.db.entity.Config;

public interface ConfigRepository extends JpaRepository<Config, String> {
}