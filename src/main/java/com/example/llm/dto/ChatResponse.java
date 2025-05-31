package com.example.llm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    private String id;
    
    private String object;
    
    private Long created;
    
    private String model;
    
    private String provider;
    
    private List<Choice> choices;
    
    private Usage usage;
    
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private Integer index;
        
        private ChatRequest.Message message;
        
        @JsonProperty("finish_reason")
        private String finishReason;
        
        private Double logprobs;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}
