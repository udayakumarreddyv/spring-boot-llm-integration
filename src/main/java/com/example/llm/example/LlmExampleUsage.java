package com.example.llm.example;

import com.example.llm.dto.ChatRequest;
import com.example.llm.dto.ChatResponse;
import com.example.llm.service.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Example usage of the LLM integration service
 * This class demonstrates how to use the LlmService to interact with different providers
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LlmExampleUsage implements CommandLineRunner {
    
    private final LlmService llmService;
    
    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "demo".equals(args[0])) {
            runDemo();
        }
    }
    
    private void runDemo() {
        log.info("Starting LLM Integration Demo...");
        
        // Check available providers
        String[] providers = llmService.getAvailableProviders();
        log.info("Available providers: {}", String.join(", ", providers));
        
        if (providers.length == 0) {
            log.warn("No providers are configured and enabled. Please check your configuration.");
            return;
        }
        
        // Example chat request
        ChatRequest request = ChatRequest.builder()
                .messages(Arrays.asList(
                    ChatRequest.Message.builder()
                        .role("user")
                        .content("Hello! Can you explain what a Spring Boot application is in one sentence?")
                        .build()
                ))
                .maxTokens(150)
                .temperature(0.7)
                .build();
        
        // Test each available provider
        for (String provider : providers) {
            if (llmService.isProviderAvailable(provider)) {
                testProvider(provider, request);
            }
        }
        
        log.info("Demo completed!");
    }
    
    private void testProvider(String provider, ChatRequest request) {
        log.info("Testing provider: {}", provider);
        
        try {
            ChatResponse response = llmService.chatCompletion(request, provider).block();
            
            if (response != null && !response.getChoices().isEmpty()) {
                String content = response.getChoices().get(0).getMessage().getContent();
                log.info("Response from {}: {}", provider, content);
                
                if (response.getUsage() != null) {
                    log.info("Token usage - Prompt: {}, Completion: {}, Total: {}", 
                            response.getUsage().getPromptTokens(),
                            response.getUsage().getCompletionTokens(),
                            response.getUsage().getTotalTokens());
                }
            } else {
                log.warn("Empty response from provider: {}", provider);
            }
            
        } catch (Exception e) {
            log.error("Error testing provider {}: {}", provider, e.getMessage());
        }
    }
}
