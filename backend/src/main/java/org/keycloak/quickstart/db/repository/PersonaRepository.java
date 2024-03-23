package org.keycloak.quickstart.db.repository;

import org.keycloak.quickstart.db.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, String> {
}
