package org.keycloak.quickstart.db.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_chat")
@Setter
@Getter
public class UserChat {
    @Id
    @Column(length = 100)
    private String uuid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "user_number", length = 100)
    private String userNumber;

    @Column(name = "message_in", columnDefinition = "TEXT")
    private String messageIn;

    @Column(name = "message_out", columnDefinition = "TEXT")
    private String messageOut;

    @Column(name = "message_id")
    private Integer messageId;

    @Column(name = "parent_message_id")
    private Integer parentMessageId;
}