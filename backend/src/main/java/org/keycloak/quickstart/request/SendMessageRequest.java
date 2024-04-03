package org.keycloak.quickstart.request;

import java.util.List;

import org.keycloak.quickstart.request.CreateConnectorRequest.ConnectorSpecificConfig;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SendMessageRequest {
    @JsonProperty("chat_session_id")
    private Integer chatSessionId;

    private String message;

    @JsonProperty("parent_message_id")
    private String parentMessageId;

    @JsonProperty("prompt_id")
    private Integer promptId;

    @JsonProperty("retrieval_options")
    private RetrievalOptions retrievalOptions;

    @Setter
    @Getter
    public static class RetrievalOptions {

        private Filters filters;
        
        @Setter
        @Getter
        public static class Filters {
            @JsonProperty("document_set")
            private String documentSet;

            @JsonProperty("source_type")
            private String sourceType;

            private String[] tags;

            @JsonProperty("time_cutoff")
            private String timeCutoff;
        }

        @JsonProperty("real_time")
        private boolean realTime = false;

        @JsonProperty("run_search")
        private String runSearch = "auto";
    }

    @JsonProperty("search_doc_ids")
    private String searchDocIds;
}

