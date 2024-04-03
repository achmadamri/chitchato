package com.api.cct.backend.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.cct.backend.db.entity.Persona;

public interface PersonaRepository extends JpaRepository<Persona, String> {
}
