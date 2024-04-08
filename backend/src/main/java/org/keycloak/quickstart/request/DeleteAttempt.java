package org.keycloak.quickstart.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeleteAttempt {
    @JsonProperty("connector_id")
    private Integer connectorId;
    
    @JsonProperty("credential_id")
    private Integer credentialId;
}

