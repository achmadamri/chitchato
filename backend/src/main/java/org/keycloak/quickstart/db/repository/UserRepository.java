package org.keycloak.quickstart.db.repository;

import org.keycloak.quickstart.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
