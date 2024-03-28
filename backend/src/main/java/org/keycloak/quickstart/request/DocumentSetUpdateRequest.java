package org.keycloak.quickstart.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DocumentSetUpdateRequest {
    private Integer id;
    
    private String description;

    @JsonProperty("cc_pair_ids")
    private List<Integer> ccPairIds;
}
