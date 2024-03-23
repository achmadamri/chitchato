package org.keycloak.quickstart.db.repository;

import org.keycloak.quickstart.db.entity.Connector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectorRepository extends JpaRepository<Connector, String> {
}