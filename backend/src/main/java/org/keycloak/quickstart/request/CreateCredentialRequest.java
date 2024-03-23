package org.keycloak.quickstart.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateCredentialRequest {
    @JsonProperty("credential_json")
    private Map<String, Object> credentialJson;

    @JsonProperty("admin_public")
    private boolean adminPublic;
}

