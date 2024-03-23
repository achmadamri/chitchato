package org.keycloak.quickstart.db.repository;

import org.keycloak.quickstart.db.entity.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromptRepository extends JpaRepository<Prompt, String> {
}
