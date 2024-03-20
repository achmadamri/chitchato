package org.keycloak.quickstart;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateConnectorCredentialRequest {

    private String name;

    @JsonProperty("is_public")
    private boolean isPublic;

    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

