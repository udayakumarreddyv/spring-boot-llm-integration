package com.example.llm.model;

public enum LlmProvider {
    OPENAI("openai"),
    ANTHROPIC("anthropic"),
    OLLAMA("ollama"),
    AZURE_OPENAI("azure-openai"),
    HUGGINGFACE("huggingface");
    
    private final String value;
    
    LlmProvider(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static LlmProvider fromValue(String value) {
        for (LlmProvider provider : values()) {
            if (provider.value.equals(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown provider: " + value);
    }
}
