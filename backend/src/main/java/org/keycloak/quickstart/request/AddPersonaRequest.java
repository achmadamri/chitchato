package org.keycloak.quickstart.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddPersonaRequest {
    private String name;
    private String description;
}
