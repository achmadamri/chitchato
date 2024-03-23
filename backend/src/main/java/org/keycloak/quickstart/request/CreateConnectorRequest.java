package org.keycloak.quickstart.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateConnectorRequest {
    private String name;
    
    private String source;

    @JsonProperty("input_type")
    private String inputType;
    
    @JsonProperty("connector_specific_config")
    private ConnectorSpecificConfig connectorSpecificConfig;
    
    private Boolean disabled;
    
    @JsonProperty("refresh_freq")
    private Object refreshFreq;

    @Setter
    @Getter
    public static class ConnectorSpecificConfig {

        @JsonProperty("file_locations")
        private List<String> fileLocations;
    }
}

