package com.api.cct.backend.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api.cct.backend.db.entity.UserChat;

public interface UserChatRepository extends JpaRepository<UserChat, String> {
    // Find one by created by and user number order by message id desc
    @Query("SELECT u FROM UserChat u WHERE u.createdBy = :createdBy AND u.userNumber = :userNumber ORDER BY u.messageId DESC")
    List<UserChat> findOneByCreatedByAndUserNumberOrderByMessageIdDesc(String createdBy, String userNumber);
}
