package org.keycloak.quickstart.db.repository;

import java.util.Optional;

import org.keycloak.quickstart.db.entity.UserDanswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDanswerRepository extends JpaRepository<UserDanswer, String> {
}
