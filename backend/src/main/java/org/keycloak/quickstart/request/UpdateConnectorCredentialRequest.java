package org.keycloak.quickstart.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateConnectorCredentialRequest {

    private String name;

    @JsonProperty("is_public")
    private boolean isPublic;

    private int id;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

