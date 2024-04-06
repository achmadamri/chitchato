package org.keycloak.quickstart.response;

import java.util.List;

import org.keycloak.quickstart.db.entity.Persona;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetPersonaListResponse {
    private List<Persona> lstPersona;
}