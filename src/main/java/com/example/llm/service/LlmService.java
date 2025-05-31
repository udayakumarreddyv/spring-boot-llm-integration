package com.example.llm.service;

import com.example.llm.dto.ChatRequest;
import com.example.llm.dto.ChatResponse;
import reactor.core.publisher.Mono;

public interface LlmService {
    
    /**
     * Send a chat completion request to the specified LLM provider
     * 
     * @param request the chat request
     * @param provider the LLM provider to use (optional, uses default if null)
     * @return the chat response
     */
    Mono<ChatResponse> chatCompletion(ChatRequest request, String provider);
    
    /**
     * Send a chat completion request using the default provider
     * 
     * @param request the chat request
     * @return the chat response
     */
    Mono<ChatResponse> chatCompletion(ChatRequest request);
    
    /**
     * Check if a provider is available and configured
     * 
     * @param provider the provider name
     * @return true if available
     */
    boolean isProviderAvailable(String provider);
    
    /**
     * Get available providers
     * 
     * @return array of available provider names
     */
    String[] getAvailableProviders();
}
