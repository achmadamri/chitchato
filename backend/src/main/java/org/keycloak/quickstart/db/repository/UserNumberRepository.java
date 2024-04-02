package org.keycloak.quickstart.db.repository;

import org.keycloak.quickstart.db.entity.UserNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNumberRepository extends JpaRepository<UserNumber, String> {
}
