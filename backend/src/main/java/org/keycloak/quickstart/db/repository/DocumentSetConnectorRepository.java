package org.keycloak.quickstart.db.repository;

import org.keycloak.quickstart.db.entity.DocumentSetConnector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentSetConnectorRepository extends JpaRepository<DocumentSetConnector, String> {
}