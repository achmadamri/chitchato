package com.api.cct.backend.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.cct.backend.db.entity.DocumentSet;

public interface DocumentSetRepository extends JpaRepository<DocumentSet, String> {
}