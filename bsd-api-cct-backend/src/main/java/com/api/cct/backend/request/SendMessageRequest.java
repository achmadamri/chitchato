package com.api.cct.backend.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendMessageRequest {
    @JsonProperty("chat_session_id")
    private Integer chatSessionId;

    private String message;

    @JsonProperty("parent_message_id")
    private Integer parentMessageId;

    @JsonProperty("prompt_id")
    private Integer promptId;

    @JsonProperty("retrieval_options")
    private RetrievalOptions retrievalOptions;

    public static class RetrievalOptions {

        private Filters filters;
        
        public static class Filters {
            @JsonProperty("document_set")
            private String documentSet;

            @JsonProperty("source_type")
            private String sourceType;

            private String[] tags;

            @JsonProperty("time_cutoff")
            private String timeCutoff;

            public String getDocumentSet() {
                return documentSet;
            }

            public void setDocumentSet(String documentSet) {
                this.documentSet = documentSet;
            }

            public String getSourceType() {
                return sourceType;
            }

            public void setSourceType(String sourceType) {
                this.sourceType = sourceType;
            }

            public String[] getTags() {
                return tags;
            }

            public void setTags(String[] tags) {
                this.tags = tags;
            }

            public String getTimeCutoff() {
                return timeCutoff;
            }

            public void setTimeCutoff(String timeCutoff) {
                this.timeCutoff = timeCutoff;
            }            
        }

        @JsonProperty("real_time")
        private boolean realTime = false;

        @JsonProperty("run_search")
        private String runSearch = "auto";

        public Filters getFilters() {
            return filters;
        }

        public void setFilters(Filters filters) {
            this.filters = filters;
        }

        public boolean isRealTime() {
            return realTime;
        }

        public void setRealTime(boolean realTime) {
            this.realTime = realTime;
        }

        public String getRunSearch() {
            return runSearch;
        }

        public void setRunSearch(String runSearch) {
            this.runSearch = runSearch;
        }        
    }

    @JsonProperty("search_doc_ids")
    private String searchDocIds;

    public Integer getChatSessionId() {
        return chatSessionId;
    }

    public void setChatSessionId(Integer chatSessionId) {
        this.chatSessionId = chatSessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(Integer parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    public Integer getPromptId() {
        return promptId;
    }

    public void setPromptId(Integer promptId) {
        this.promptId = promptId;
    }

    public RetrievalOptions getRetrievalOptions() {
        return retrievalOptions;
    }

    public void setRetrievalOptions(RetrievalOptions retrievalOptions) {
        this.retrievalOptions = retrievalOptions;
    }

    public String getSearchDocIds() {
        return searchDocIds;
    }

    public void setSearchDocIds(String searchDocIds) {
        this.searchDocIds = searchDocIds;
    }    
}

