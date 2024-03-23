package org.keycloak.quickstart.db.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "persona")
@Setter
@Getter
public class Persona {
    @Id
    private String uuid;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updateAt;

    private String updatedBy;

    private Integer personaId;

    private String name;

    private String description;

    private Integer promptId;

    private Integer documentSetId;
}