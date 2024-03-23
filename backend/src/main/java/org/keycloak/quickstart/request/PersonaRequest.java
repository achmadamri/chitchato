package org.keycloak.quickstart.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PersonaRequest {
    private String name;

    private String description;

    private Boolean shared;

    @JsonProperty("num_chunks")
    private Integer numChunks;

    @JsonProperty("llm_relevance_filter")
    private Boolean llmRelevanceFilter;

    @JsonProperty("llm_filter_extraction")
    private Boolean llmFilterExtraction;

    @JsonProperty("recency_bias")
    private String recencyBias;

    @JsonProperty("prompt_ids")
    private List<Integer> promptIds;

    @JsonProperty("document_set_ids")
    private List<Integer> documentSetIds;

    @JsonProperty("llm_model_version_override")
    private String llmModelVersionOverride;

    @JsonProperty("starter_messages")
    private List<String> starterMessages;
}