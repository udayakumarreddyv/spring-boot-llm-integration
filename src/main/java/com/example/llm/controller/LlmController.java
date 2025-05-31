package com.example.llm.controller;

import com.example.llm.dto.ChatRequest;
import com.example.llm.dto.ChatResponse;
import com.example.llm.service.LlmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/llm")
@RequiredArgsConstructor
@Tag(name = "LLM Integration", description = "APIs for integrating with OpenAI-compatible LLM providers")
public class LlmController {
    
    private final LlmService llmService;
    
    @PostMapping("/chat/completions")
    @Operation(summary = "Create chat completion", 
               description = "Send a chat completion request to the configured LLM provider")
    public Mono<ResponseEntity<ChatResponse>> chatCompletion(
            @Valid @RequestBody ChatRequest request,
            @Parameter(description = "LLM provider to use (optional, uses default if not specified)")
            @RequestParam(required = false) String provider) {
        
        log.info("Received chat completion request for provider: {}", 
                provider != null ? provider : "default");
        
        return llmService.chatCompletion(request, provider)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.debug("Successfully processed chat completion"))
                .doOnError(error -> log.error("Error processing chat completion: {}", error.getMessage()));
    }
    
    @GetMapping("/providers")
    @Operation(summary = "Get available providers", 
               description = "List all configured and available LLM providers")
    public ResponseEntity<List<String>> getAvailableProviders() {
        String[] providers = llmService.getAvailableProviders();
        return ResponseEntity.ok(Arrays.asList(providers));
    }
    
    @GetMapping("/providers/{provider}/status")
    @Operation(summary = "Check provider status", 
               description = "Check if a specific provider is available and configured")
    public ResponseEntity<ProviderStatus> getProviderStatus(
            @Parameter(description = "Provider name to check")
            @PathVariable String provider) {
        
        boolean available = llmService.isProviderAvailable(provider);
        ProviderStatus status = new ProviderStatus(provider, available);
        
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", 
               description = "Check the health status of the LLM integration service")
    public ResponseEntity<HealthStatus> healthCheck() {
        String[] availableProviders = llmService.getAvailableProviders();
        boolean healthy = availableProviders.length > 0;
        
        HealthStatus health = new HealthStatus(
                healthy ? "UP" : "DOWN",
                availableProviders.length,
                Arrays.asList(availableProviders)
        );
        
        return ResponseEntity.ok(health);
    }
    
    // Response DTOs
    @Data
    @AllArgsConstructor
    public static class ProviderStatus {
        private String provider;
        private boolean available;
    }
    
    @Data
    @AllArgsConstructor
    public static class HealthStatus {
        private String status;
        private int availableProviders;
        private List<String> providers;
    }
}
