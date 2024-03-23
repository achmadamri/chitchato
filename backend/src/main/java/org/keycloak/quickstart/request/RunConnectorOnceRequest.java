package org.keycloak.quickstart.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RunConnectorOnceRequest {

    @JsonProperty("connector_id")
    private int connectorId;

    @JsonProperty("credentialIds")
    private List<Integer> credentialIds;

    @JsonProperty("from_beginning")
    private boolean fromBeginning;
}