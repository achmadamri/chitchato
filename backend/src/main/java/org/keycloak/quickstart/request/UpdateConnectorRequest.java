package org.keycloak.quickstart.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateConnectorRequest {
    private String name;
    
    private String source;

    @JsonProperty("input_type")
    private String inputType = "load_status";
    
    @JsonProperty("connector_specific_config")
    private ConnectorSpecificConfig connectorSpecificConfig;
    
    @JsonProperty("refresh_freq")
    private Object refreshFreq;
    
    private Boolean disabled;

    private Integer id;

    @JsonProperty("credential_ids")
    private List<Integer> credentialIds;

    @JsonProperty("time_created")
    private String timeCreated;

    @JsonProperty("time_updated")
    private String timeUpdated;

    @Setter
    @Getter
    public static class ConnectorSpecificConfig {

        @JsonProperty("file_locations")
        private List<String> fileLocations;
    }
}

