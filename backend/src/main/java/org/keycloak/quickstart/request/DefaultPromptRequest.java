package org.keycloak.quickstart.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DefaultPromptRequest {

    private String name;
    
    private String description;
    
    private Boolean shared;
    
    @JsonProperty("system_prompt")
    private String systemPrompt;
    
    @JsonProperty("task_prompt")
    private String taskPrompt;

    @JsonProperty("include_citations")
    private Boolean includeCitations;

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

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public String getTaskPrompt() {
        return taskPrompt;
    }

    public void setTaskPrompt(String taskPrompt) {
        this.taskPrompt = taskPrompt;
    }

    public Boolean getIncludeCitations() {
        return includeCitations;
    }

    public void setIncludeCitations(Boolean includeCitations) {
        this.includeCitations = includeCitations;
    }
}

