package org.keycloak.quickstart;

import java.util.List;

public class ConnectorConfig {

    private String name;
    private String source;
    private String inputType;
    private ConnectorSpecificConfig connectorSpecificConfig;
    private Boolean disabled;
    private Object refreshFreq;

    public static class ConnectorSpecificConfig {
        private List<String> fileLocations;

        public List<String> getFileLocations() {
            return fileLocations;
        }

        public void setFileLocations(List<String> fileLocations) {
            this.fileLocations = fileLocations;
        }        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Object getRefreshFreq() {
        return refreshFreq;
    }

    public void setRefreshFreq(Object refreshFreq) {
        this.refreshFreq = refreshFreq;
    }

    public ConnectorSpecificConfig getConnectorSpecificConfig() {
        return connectorSpecificConfig;
    }

    public void setConnectorSpecificConfig(ConnectorSpecificConfig connectorSpecificConfig) {
        this.connectorSpecificConfig = connectorSpecificConfig;
    }    
}

