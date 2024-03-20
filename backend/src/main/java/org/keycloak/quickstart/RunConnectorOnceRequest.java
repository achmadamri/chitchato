package org.keycloak.quickstart;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RunConnectorOnceRequest {

    @JsonProperty("connector_id")
    private int connectorId;

    @JsonProperty("credentialIds")
    private List<Integer> credentialIds;

    @JsonProperty("from_beginning")
    private boolean fromBeginning;

    public int getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(int connectorId) {
        this.connectorId = connectorId;
    }

    public List<Integer> getCredentialIds() {
        return credentialIds;
    }

    public void setCredentialIds(List<Integer> credentialIds) {
        this.credentialIds = credentialIds;
    }

    public boolean isFromBeginning() {
        return fromBeginning;
    }

    public void setFromBeginning(boolean fromBeginning) {
        this.fromBeginning = fromBeginning;
    }    
}