package org.keycloak.quickstart.request;

import java.util.List;

import org.keycloak.quickstart.request.CreateConnectorRequest.ConnectorSpecificConfig;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SendMessageRequest {
    private Integer chatSessionId;

    private String message;

    private String parentMessageId;

    private String promptId;

    @JsonProperty("retrieval_options")
    private RetrievalOptions retrievalOptions;

    @Setter
    @Getter
    public static class RetrievalOptions {

        @JsonProperty("filters")
        private Filters filters;
        
        @Setter
        @Getter
        public static class Filters {
            private String documentSet;

            private String sourceType;

            private String[] tags;

            private String timeCutoff;
        }

        private boolean realTime = false;

        private String runSearch = "auto";
    }

    private String searchDocIds;
}

