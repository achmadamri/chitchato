package org.keycloak.quickstart.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "config")
@Setter
@Getter
public class Config {    
    @Id
    private String key;

    private String value;
}

