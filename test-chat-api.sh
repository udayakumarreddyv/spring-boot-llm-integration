#!/bin/bash

# Test Chat Completions API
echo "=== Testing Spring Boot LLM Integration API ==="
echo

# Test health endpoint
echo "1. Testing health endpoint:"
curl -s http://localhost:8080/api/v1/llm/health | jq .
echo

# Test providers endpoint
echo "2. Testing providers endpoint:"
curl -s http://localhost:8080/api/v1/llm/providers | jq .
echo

# Test chat completion with default provider (will fail without real API key)
echo "3. Testing chat completion (will fail without real API key):"
curl -s -X POST http://localhost:8080/api/v1/llm/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {
        "role": "user",
        "content": "Hello, how are you?"
      }
    ],
    "maxTokens": 100,
    "temperature": 0.7
  }' | jq .
echo

# Test with specific provider
echo "4. Testing chat completion with specific provider (anthropic):"
curl -s -X POST "http://localhost:8080/api/v1/llm/chat/completions?provider=anthropic" \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {
        "role": "user", 
        "content": "What is Spring Boot?"
      }
    ],
    "maxTokens": 150,
    "temperature": 0.5
  }' | jq .
echo

echo "=== API Testing Complete ==="
