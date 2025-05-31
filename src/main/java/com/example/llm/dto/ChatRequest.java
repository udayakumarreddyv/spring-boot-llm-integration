package com.example.llm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    
    @NotNull
    private List<Message> messages;
    
    private String model;
    
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    
    private Double temperature;
    
    private Boolean stream;
    
    private String provider;
    
    @JsonProperty("top_p")
    private Double topP;
    
    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;
    
    @JsonProperty("presence_penalty")
    private Double presencePenalty;
    
    private List<String> stop;
    
    private String user;
    
    // Additional provider-specific parameters
    private Map<String, Object> additionalProperties;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        @NotBlank
        private String role; // "system", "user", "assistant"
        
        @NotBlank
        private String content;
        
        private String name;
    }
}
