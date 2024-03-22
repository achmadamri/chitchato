package org.keycloak.quickstart.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentSetRequest {

    private String name;
    
    private String description;

    @JsonProperty("cc_pair_ids")
    private List<Integer> ccPairIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Integer> getCcPairIds() {
        return ccPairIds;
    }

    public void setCcPairIds(List<Integer> ccPairIds) {
        this.ccPairIds = ccPairIds;
    }

}
