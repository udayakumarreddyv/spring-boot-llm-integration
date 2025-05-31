package com.example.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LlmProperties {
    
    private Map<String, ProviderConfig> providers;
    private String defaultProvider = "openai";
    private Integer maxTokens = 1000;
    private Double temperature = 0.7;
    private Boolean stream = false;
    
    @Data
    public static class ProviderConfig {
        private Boolean enabled = false;
        private String baseUrl;
        private String apiKey;
        private String defaultModel;
        private Integer timeout = 30000;
    }
}
