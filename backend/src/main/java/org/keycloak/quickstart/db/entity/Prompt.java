package org.keycloak.quickstart.db.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "prompt")
@Setter
@Getter
public class Prompt {
    @Id
    private String uuid;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updateAt;

    private String updatedBy;

    private Integer promptId;

    private String name;

    private String description;

    private String systemPrompt;

    private String taskPrompt;
}