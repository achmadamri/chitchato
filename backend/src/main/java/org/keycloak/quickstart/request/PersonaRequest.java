package org.keycloak.quickstart.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public Integer getNumChunks() {
        return numChunks;
    }

    public void setNumChunks(Integer numChunks) {
        this.numChunks = numChunks;
    }

    public Boolean getLlmRelevanceFilter() {
        return llmRelevanceFilter;
    }

    public void setLlmRelevanceFilter(Boolean llmRelevanceFilter) {
        this.llmRelevanceFilter = llmRelevanceFilter;
    }

    public Boolean getLlmFilterExtraction() {
        return llmFilterExtraction;
    }

    public void setLlmFilterExtraction(Boolean llmFilterExtraction) {
        this.llmFilterExtraction = llmFilterExtraction;
    }

    public String getRecencyBias() {
        return recencyBias;
    }

    public void setRecencyBias(String recencyBias) {
        this.recencyBias = recencyBias;
    }

    public List<Integer> getPromptIds() {
        return promptIds;
    }

    public void setPromptIds(List<Integer> promptIds) {
        this.promptIds = promptIds;
    }

    public List<Integer> getDocumentSetIds() {
        return documentSetIds;
    }

    public void setDocumentSetIds(List<Integer> documentSetIds) {
        this.documentSetIds = documentSetIds;
    }

    public String getLlmModelVersionOverride() {
        return llmModelVersionOverride;
    }

    public void setLlmModelVersionOverride(String llmModelVersionOverride) {
        this.llmModelVersionOverride = llmModelVersionOverride;
    }

    public List<String> getStarterMessages() {
        return starterMessages;
    }

    public void setStarterMessages(List<String> starterMessages) {
        this.starterMessages = starterMessages;
    }
}

