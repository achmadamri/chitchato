package org.keycloak.quickstart.db.repository;

import org.keycloak.quickstart.db.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<Config, String> {
}