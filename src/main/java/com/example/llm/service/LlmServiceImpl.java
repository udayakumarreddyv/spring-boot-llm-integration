package com.example.llm.service;

import com.example.llm.config.LlmProperties;
import com.example.llm.dto.ChatRequest;
import com.example.llm.dto.ChatResponse;
import com.example.llm.exception.LlmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmServiceImpl implements LlmService {
    
    private final LlmProperties llmProperties;
    private final WebClient webClient;
    
    @Override
    public Mono<ChatResponse> chatCompletion(ChatRequest request, String provider) {
        String targetProvider = StringUtils.hasText(provider) ? provider : llmProperties.getDefaultProvider();
        
        if (!isProviderAvailable(targetProvider)) {
            return Mono.error(new LlmException(
                    "Provider '" + targetProvider + "' is not available or not configured", 
                    targetProvider, 
                    400
            ));
        }
        
        LlmProperties.ProviderConfig config = llmProperties.getProviders().get(targetProvider);
        
        // Prepare the request
        ChatRequest processedRequest = processRequest(request, config, targetProvider);
        
        log.debug("Sending request to provider '{}' with model '{}'", targetProvider, processedRequest.getModel());
        
        return webClient.post()
                .uri(config.getBaseUrl() + "/chat/completions")
                .headers(headers -> configureHeaders(headers, config, targetProvider))
                .bodyValue(processedRequest)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .map(response -> {
                    response.setProvider(targetProvider);
                    return response;
                })
                .timeout(Duration.ofMillis(config.getTimeout()))
                .onErrorMap(WebClientResponseException.class, ex -> 
                    new LlmException(
                            "Provider error: " + ex.getResponseBodyAsString(), 
                            targetProvider, 
                            ex.getStatusCode().value(), 
                            ex
                    )
                )
                .onErrorMap(Exception.class, ex -> 
                    !(ex instanceof LlmException) ? 
                        new LlmException("Unexpected error: " + ex.getMessage(), targetProvider, ex) : 
                        ex
                );
    }
    
    @Override
    public Mono<ChatResponse> chatCompletion(ChatRequest request) {
        return chatCompletion(request, null);
    }
    
    @Override
    public boolean isProviderAvailable(String provider) {
        if (!StringUtils.hasText(provider)) {
            return false;
        }
        
        LlmProperties.ProviderConfig config = llmProperties.getProviders().get(provider);
        return config != null && 
               Boolean.TRUE.equals(config.getEnabled()) && 
               StringUtils.hasText(config.getBaseUrl()) &&
               StringUtils.hasText(config.getApiKey());
    }
    
    @Override
    public String[] getAvailableProviders() {
        return llmProperties.getProviders().entrySet().stream()
                .filter(entry -> Boolean.TRUE.equals(entry.getValue().getEnabled()))
                .map(Map.Entry::getKey)
                .toArray(String[]::new);
    }
    
    private ChatRequest processRequest(ChatRequest request, LlmProperties.ProviderConfig config, String provider) {
        ChatRequest.ChatRequestBuilder builder = ChatRequest.builder()
                .messages(request.getMessages())
                .model(StringUtils.hasText(request.getModel()) ? request.getModel() : config.getDefaultModel())
                .maxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : llmProperties.getMaxTokens())
                .temperature(request.getTemperature() != null ? request.getTemperature() : llmProperties.getTemperature())
                .stream(request.getStream() != null ? request.getStream() : llmProperties.getStream())
                .topP(request.getTopP())
                .frequencyPenalty(request.getFrequencyPenalty())
                .presencePenalty(request.getPresencePenalty())
                .stop(request.getStop())
                .user(request.getUser());
        
        // Handle provider-specific adjustments
        if ("anthropic".equals(provider)) {
            // Anthropic uses different parameter names
            builder.additionalProperties(createAnthropicSpecificParams(request));
        }
        
        return builder.build();
    }
    
    private Map<String, Object> createAnthropicSpecificParams(ChatRequest request) {
        Map<String, Object> params = new HashMap<>();
        // Add Anthropic-specific parameters if needed
        return params;
    }
    
    private void configureHeaders(org.springframework.http.HttpHeaders headers, 
                                LlmProperties.ProviderConfig config, 
                                String provider) {
        headers.set("Content-Type", "application/json");
        
        switch (provider) {
            case "openai":
            case "azure-openai":
            case "ollama":
                headers.set("Authorization", "Bearer " + config.getApiKey());
                break;
            case "anthropic":
                headers.set("x-api-key", config.getApiKey());
                headers.set("anthropic-version", "2023-06-01");
                break;
            case "huggingface":
                headers.set("Authorization", "Bearer " + config.getApiKey());
                break;
            default:
                headers.set("Authorization", "Bearer " + config.getApiKey());
        }
    }
}
