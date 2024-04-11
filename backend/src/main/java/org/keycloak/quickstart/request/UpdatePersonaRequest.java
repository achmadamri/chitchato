package org.keycloak.quickstart.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdatePersonaRequest {
    private String personaUuid;
    private String name;
    private String description;
    private String systemPrompt;
    private String taskPrompt;
}
