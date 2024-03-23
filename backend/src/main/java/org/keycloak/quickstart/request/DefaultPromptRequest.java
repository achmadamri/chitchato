package org.keycloak.quickstart.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
}

