# Spring Boot LLM Integration - API Usage Guide

## 📋 Overview
This Spring Boot application provides a unified API for integrating with multiple OpenAI-compatible LLM providers including OpenAI, Anthropic, Ollama, Azure OpenAI, and HuggingFace.

## 🚀 Quick Start

### 1. Start the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 2. View API Documentation
Open Swagger UI: `http://localhost:8080/swagger-ui.html`

### 3. Health Check
```bash
curl http://localhost:8080/actuator/health
```

## 🛠️ API Endpoints

### Health and Status

#### Application Health
```bash
GET /actuator/health
```

#### LLM Service Health
```bash
GET /api/v1/llm/health
```
Response:
```json
{
  "status": "UP",
  "availableProviders": 3,
  "providers": ["openai", "anthropic", "ollama"]
}
```

#### Available Providers
```bash
GET /api/v1/llm/providers
```
Response:
```json
["openai", "anthropic", "ollama"]
```

## 💬 Chat Completions API

### Basic Request Structure
```bash
POST /api/v1/llm/chat/completions
Content-Type: application/json

{
  "messages": [
    {
      "role": "user",
      "content": "Your message here"
    }
  ],
  "provider": "optional-provider-name",
  "model": "optional-model-override",
  "maxTokens": 100,
  "temperature": 0.7,
  "stream": false
}
```

### Request Parameters

| Parameter | Type | Required | Description | Default |
|-----------|------|----------|-------------|---------|
| `messages` | Array | Yes | Array of message objects | - |
| `provider` | String | No | Provider to use (openai, anthropic, ollama, etc.) | Default provider from config |
| `model` | String | No | Model name override | Provider's default model |
| `maxTokens` | Integer | No | Maximum tokens to generate | 1000 |
| `temperature` | Float | No | Randomness (0.0-2.0) | 0.7 |
| `stream` | Boolean | No | Enable streaming response | false |

### Message Object Structure
```json
{
  "role": "user|assistant|system",
  "content": "Message content"
}
```

### Response Structure
```json
{
  "id": "chatcmpl-123",
  "object": "chat.completion",
  "created": 1677652288,
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "Generated response text"
      },
      "finishReason": "stop"
    }
  ],
  "usage": {
    "promptTokens": 56,
    "completionTokens": 31,
    "totalTokens": 87
  }
}
```

## 📝 Complete Examples

### Example 1: Simple Question with Default Provider
```bash
curl -X POST http://localhost:8080/api/v1/llm/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {
        "role": "user",
        "content": "What is the capital of France?"
      }
    ],
    "maxTokens": 50
  }'
```

### Example 2: Using Specific Provider (OpenAI)
```bash
curl -X POST http://localhost:8080/api/v1/llm/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {
        "role": "user",
        "content": "Explain quantum computing in simple terms"
      }
    ],
    "provider": "openai",
    "maxTokens": 200,
    "temperature": 0.5
  }'
```

### Example 3: Using Anthropic Claude
```bash
curl -X POST http://localhost:8080/api/v1/llm/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {
        "role": "user",
        "content": "Write a haiku about spring"
      }
    ],
    "provider": "anthropic",
    "model": "claude-3-sonnet-20240229",
    "maxTokens": 100,
    "temperature": 0.8
  }'
```

### Example 4: Multi-turn Conversation
```bash
curl -X POST http://localhost:8080/api/v1/llm/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {
        "role": "system",
        "content": "You are a helpful programming assistant."
      },
      {
        "role": "user",
        "content": "How do I create a REST API in Spring Boot?"
      },
      {
        "role": "assistant",
        "content": "To create a REST API in Spring Boot, you need to..."
      },
      {
        "role": "user",
        "content": "Can you show me a complete example?"
      }
    ],
    "provider": "ollama",
    "maxTokens": 500
  }'
```

### Example 5: Using Local Ollama
```bash
curl -X POST http://localhost:8080/api/v1/llm/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {
        "role": "user",
        "content": "Summarize the benefits of microservices architecture"
      }
    ],
    "provider": "ollama",
    "model": "llama2",
    "maxTokens": 300,
    "temperature": 0.3
  }'
```

## ⚠️ Error Handling

### Error Response Structure
All errors return a consistent JSON structure:
```json
{
  "message": "Error description",
  "source": "error-source",
  "statusCode": 400,
  "timestamp": "2025-05-28T10:30:00"
}
```

### Common Error Scenarios

#### 1. Invalid Provider (400 Bad Request)
```bash
# Request
curl -X POST http://localhost:8080/api/v1/llm/chat/completions?provider=invalid \
  -H "Content-Type: application/json" \
  -d '{"messages":[{"role":"user","content":"Hello"}]}'

# Response
{
  "message": "Provider 'invalid' is not available or not configured",
  "source": "invalid",
  "statusCode": 400,
  "timestamp": "2025-05-28T10:30:00"
}
```

#### 2. Missing API Key (500 Internal Server Error)
```json
{
  "message": "Provider configuration error: Missing API key for provider 'openai'",
  "source": "openai",
  "statusCode": 500,
  "timestamp": "2025-05-28T10:30:00"
}
```

#### 3. Rate Limiting (429 Too Many Requests)
```json
{
  "message": "Rate limit exceeded for provider 'openai'",
  "source": "openai",
  "statusCode": 429,
  "timestamp": "2025-05-28T10:30:00"
}
```

#### 4. Provider Timeout (504 Gateway Timeout)
```json
{
  "message": "Request timeout for provider 'anthropic'",
  "source": "anthropic",
  "statusCode": 504,
  "timestamp": "2025-05-28T10:30:00"
}
```

## ⚙️ Configuration

### Environment Variables
Set these environment variables to configure API keys:

```bash
export OPENAI_API_KEY="sk-..."
export ANTHROPIC_API_KEY="sk-ant-api03-..."
export AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com"
export AZURE_OPENAI_API_KEY="..."
export HUGGINGFACE_API_KEY="hf_..."
```

### Provider-Specific Settings

#### OpenAI Configuration
```yaml
llm:
  providers:
    openai:
      enabled: true
      base-url: "https://api.openai.com/v1"
      api-key: "${OPENAI_API_KEY:}"
      default-model: "gpt-3.5-turbo"
      timeout: 30000
```

#### Anthropic Configuration
```yaml
llm:
  providers:
    anthropic:
      enabled: true
      base-url: "https://api.anthropic.com/v1"
      api-key: "${ANTHROPIC_API_KEY:}"
      default-model: "claude-3-sonnet-20240229"
      timeout: 30000
```

#### Ollama Configuration
```yaml
llm:
  providers:
    ollama:
      enabled: true
      base-url: "http://localhost:11434/v1"
      api-key: "ollama"
      default-model: "llama2"
      timeout: 60000
```

## 🔍 Testing and Validation

### Health Check Script
```bash
#!/bin/bash

echo "=== Application Health ==="
curl -s http://localhost:8080/actuator/health | jq .

echo -e "\n=== LLM Service Health ==="
curl -s http://localhost:8080/api/v1/llm/health | jq .

echo -e "\n=== Available Providers ==="
curl -s http://localhost:8080/api/v1/llm/providers | jq .

echo -e "\n=== Test Chat Completion ==="
curl -s -X POST http://localhost:8080/api/v1/llm/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [{"role": "user", "content": "Hello"}],
    "provider": "ollama",
    "maxTokens": 50
  }' | jq .
```

### Performance Testing
```bash
# Test response time
time curl -X POST http://localhost:8080/api/v1/llm/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [{"role": "user", "content": "What is 2+2?"}],
    "provider": "ollama",
    "maxTokens": 10
  }'
```

## 🚀 Best Practices

### 1. Provider Selection
- Use **Ollama** for local development and testing (no API key required)
- Use **OpenAI** for production applications with GPT models
- Use **Anthropic** for Claude models with better reasoning capabilities
- Use **Azure OpenAI** for enterprise deployments

### 2. Error Handling
- Always check the response status code
- Implement retry logic for transient errors (500, 502, 503, 504)
- Handle rate limiting gracefully (429)
- Log errors for debugging and monitoring

### 3. Performance Optimization
- Set appropriate timeout values based on your use case
- Use streaming for long responses when possible
- Cache responses when appropriate
- Monitor token usage to control costs

### 4. Security
- Never expose API keys in client-side code
- Use environment variables for sensitive configuration
- Implement proper authentication and authorization
- Monitor API usage and set up alerts

## 📚 Additional Resources

- **[Project Summary](PROJECT_SUMMARY.md)** - Complete project overview
- **[Main README](../README.md)** - Project setup and configuration
- **[Swagger UI](http://localhost:8080/swagger-ui.html)** - Interactive API documentation
- **[OpenAPI Spec](http://localhost:8080/v3/api-docs)** - Machine-readable API specification
