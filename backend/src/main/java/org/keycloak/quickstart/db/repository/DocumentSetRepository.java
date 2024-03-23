package org.keycloak.quickstart.db.repository;

import org.keycloak.quickstart.db.entity.DocumentSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentSetRepository extends JpaRepository<DocumentSet, String> {
}