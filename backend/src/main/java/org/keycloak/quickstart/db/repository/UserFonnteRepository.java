package org.keycloak.quickstart.db.repository;

import org.keycloak.quickstart.db.entity.UserFonnte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFonnteRepository extends JpaRepository<UserFonnte, String> {
}
