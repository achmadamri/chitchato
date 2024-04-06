package org.keycloak.quickstart.response;

import java.util.List;

import org.keycloak.quickstart.db.entity.Connector;
import org.keycloak.quickstart.db.entity.Persona;
import org.keycloak.quickstart.db.entity.Prompt;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetPersonaResponse {
    private Persona persona;

    private Prompt prompt;

    private List<Connector> lstConnector;
}