package org.keycloak.quickstart.db.repository;

import org.keycloak.quickstart.db.entity.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserChatRepository extends JpaRepository<UserChat, String> {
    // Find one by created by and user number order by message id desc
    @Query("SELECT u FROM UserChat u WHERE u.createdBy = :createdBy AND u.userNumber = :userNumber ORDER BY u.messageId DESC LIMIT 1")
    UserChat findOneByCreatedByAndUserNumberOrderByMessageIdDesc(String createdBy, String userNumber);
}
