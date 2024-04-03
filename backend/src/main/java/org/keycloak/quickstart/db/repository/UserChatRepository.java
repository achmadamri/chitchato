package org.keycloak.quickstart.db.repository;

import org.keycloak.quickstart.db.entity.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChatRepository extends JpaRepository<UserChat, String> {
}
