package org.keycloak.quickstart;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateCredentialRequest {

    @JsonProperty("credential_json")
    private Map<String, Object> credentialJson;

    @JsonProperty("admin_public")
    private boolean adminPublic;

    public Map<String, Object> getCredentialJson() {
        return credentialJson;
    }

    public void setCredentialJson(Map<String, Object> credentialJson) {
        this.credentialJson = credentialJson;
    }

    public boolean isAdminPublic() {
        return adminPublic;
    }

    public void setAdminPublic(boolean adminPublic) {
        this.adminPublic = adminPublic;
    }
}

