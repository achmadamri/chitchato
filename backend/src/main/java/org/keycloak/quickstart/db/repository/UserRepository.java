package org.keycloak.quickstart.db.repository;

import java.util.Optional;

import org.keycloak.quickstart.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    // find by username
    Optional<User> findByUsername(String username);
}
