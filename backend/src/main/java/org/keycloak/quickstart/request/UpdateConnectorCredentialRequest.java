package org.keycloak.quickstart.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateConnectorCredentialRequest {
    private String name;

    @JsonProperty("is_public")
    private boolean isPublic;

    private int connectorId;

    private int credentialId;
}