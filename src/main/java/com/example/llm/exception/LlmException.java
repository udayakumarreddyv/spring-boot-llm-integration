package com.example.llm.exception;

public class LlmException extends RuntimeException {
    
    private final String provider;
    private final int statusCode;
    
    public LlmException(String message, String provider) {
        super(message);
        this.provider = provider;
        this.statusCode = 500;
    }
    
    public LlmException(String message, String provider, int statusCode) {
        super(message);
        this.provider = provider;
        this.statusCode = statusCode;
    }
    
    public LlmException(String message, String provider, Throwable cause) {
        super(message, cause);
        this.provider = provider;
        this.statusCode = 500;
    }
    
    public LlmException(String message, String provider, int statusCode, Throwable cause) {
        super(message, cause);
        this.provider = provider;
        this.statusCode = statusCode;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}
