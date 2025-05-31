package com.example.llm.service;

import com.example.llm.config.LlmProperties;
import com.example.llm.dto.ChatRequest;
import com.example.llm.dto.ChatResponse;
import com.example.llm.exception.LlmException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LlmServiceImplTest {

    @Mock
    private LlmProperties llmProperties;
    
    private MockWebServer mockWebServer;
    private LlmServiceImpl llmService;
    private WebClient webClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        
        webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
                
        llmService = new LlmServiceImpl(llmProperties, webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnAvailableProviders() {
        // Given
        LlmProperties.ProviderConfig enabledConfig = new LlmProperties.ProviderConfig();
        enabledConfig.setEnabled(true);
        
        LlmProperties.ProviderConfig disabledConfig = new LlmProperties.ProviderConfig();
        disabledConfig.setEnabled(false);
        
        Map<String, LlmProperties.ProviderConfig> providers = new HashMap<>();
        providers.put("openai", enabledConfig);
        providers.put("anthropic", disabledConfig);
        
        when(llmProperties.getProviders()).thenReturn(providers);
        
        // When
        String[] availableProviders = llmService.getAvailableProviders();
        
        // Then
        assertThat(availableProviders).containsExactly("openai");
    }

    @Test
    void shouldCheckProviderAvailability() {
        // Given
        LlmProperties.ProviderConfig config = new LlmProperties.ProviderConfig();
        config.setEnabled(true);
        String baseUrl = mockWebServer.url("/").toString();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        config.setBaseUrl(baseUrl);
        config.setApiKey("test-key");
        
        Map<String, LlmProperties.ProviderConfig> providers = new HashMap<>();
        providers.put("openai", config);
        when(llmProperties.getProviders()).thenReturn(providers);
        
        // When & Then
        assertThat(llmService.isProviderAvailable("openai")).isTrue();
        assertThat(llmService.isProviderAvailable("nonexistent")).isFalse();
    }

    @Test
    void shouldHandleProviderNotAvailable() {
        // Given
        when(llmProperties.getProviders()).thenReturn(Collections.emptyMap());
        when(llmProperties.getDefaultProvider()).thenReturn("openai");
        
        ChatRequest request = ChatRequest.builder()
                .messages(Arrays.asList(ChatRequest.Message.builder()
                        .role("user")
                        .content("Hello")
                        .build()))
                .build();
        
        // When & Then
        StepVerifier.create(llmService.chatCompletion(request))
                .expectError(LlmException.class)
                .verify();
    }

    @Test
    void shouldReturnSuccessfulChatCompletion() throws Exception {
        // Given
        String mockResponse = "{"
            + "\"id\": \"chatcmpl-123\","
            + "\"object\": \"chat.completion\","
            + "\"created\": 1677652288,"
            + "\"model\": \"gpt-3.5-turbo\","
            + "\"choices\": [{"
            + "    \"index\": 0,"
            + "    \"message\": {"
            + "        \"role\": \"assistant\","
            + "        \"content\": \"Hello! How can I help you today?\""
            + "    },"
            + "    \"finish_reason\": \"stop\""
            + "}],"
            + "\"usage\": {"
            + "    \"prompt_tokens\": 9,"
            + "    \"completion_tokens\": 12,"
            + "    \"total_tokens\": 21"
            + "}"
            + "}";
        
        // Mock the /chat/completions endpoint
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200));
        
        LlmProperties.ProviderConfig config = new LlmProperties.ProviderConfig();
        config.setEnabled(true);
        String baseUrl = mockWebServer.url("/").toString().replaceAll("/$", "");
        config.setBaseUrl(baseUrl);
        config.setApiKey("test-key");
        config.setDefaultModel("gpt-3.5-turbo");
        config.setTimeout(10000); // Increased timeout
        
        Map<String, LlmProperties.ProviderConfig> providers = new HashMap<>();
        providers.put("openai", config);
        when(llmProperties.getProviders()).thenReturn(providers);
        when(llmProperties.getDefaultProvider()).thenReturn("openai");
        when(llmProperties.getMaxTokens()).thenReturn(1000);
        when(llmProperties.getTemperature()).thenReturn(0.7);
        when(llmProperties.getStream()).thenReturn(false);
        
        ChatRequest request = ChatRequest.builder()
                .messages(Arrays.asList(ChatRequest.Message.builder()
                        .role("user")
                        .content("Hello")
                        .build()))
                .build();
        
        // When & Then
        StepVerifier.create(llmService.chatCompletion(request))
                .expectNextCount(1)
                .verifyComplete();
                
        // Verify that a request was made to the mock server
        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
        
        // Check the request details
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getPath()).isEqualTo("/chat/completions");
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("Bearer test-key");
    }
}
