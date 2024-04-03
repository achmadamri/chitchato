package com.api.cct.backend.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.cct.backend.db.entity.Prompt;

public interface PromptRepository extends JpaRepository<Prompt, String> {
}
