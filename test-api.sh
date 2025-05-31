#!/bin/bash

# Spring Boot LLM Integration Test Script
echo "🚀 Spring Boot LLM Integration Testing Script"
echo "============================================="

# Check if application is running
echo "📋 Checking if application is running on port 8080..."
if curl -s -f http://localhost:8080/actuator/health > /dev/null; then
    echo "✅ Application is running!"
else
    echo "❌ Application is not running on port 8080"
    echo "🔧 Starting application..."
    mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev" &
    APP_PID=$!
    echo "⏳ Waiting for application to start (30 seconds)..."
    sleep 30
fi

# Test Health Endpoint
echo ""
echo "🏥 Testing Health Endpoint..."
curl -s http://localhost:8080/actuator/health | jq '.' 2>/dev/null || curl -s http://localhost:8080/actuator/health

# Test LLM Service Health
echo ""
echo "🤖 Testing LLM Service Health..."
curl -s http://localhost:8080/api/v1/llm/health | jq '.' 2>/dev/null || curl -s http://localhost:8080/api/v1/llm/health

# Test Available Providers
echo ""
echo "📦 Testing Available Providers..."
curl -s http://localhost:8080/api/v1/llm/providers | jq '.' 2>/dev/null || curl -s http://localhost:8080/api/v1/llm/providers

# Test Provider Status
echo ""
echo "🔍 Testing Provider Status (Ollama)..."
curl -s http://localhost:8080/api/v1/llm/providers/ollama/status | jq '.' 2>/dev/null || curl -s http://localhost:8080/api/v1/llm/providers/ollama/status

# Test Chat Completion (will fail without configured providers)
echo ""
echo "💬 Testing Chat Completion..."
curl -s -X POST http://localhost:8080/api/v1/llm/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {
        "role": "user",
        "content": "Hello, this is a test message"
      }
    ],
    "max_tokens": 50
  }' | jq '.' 2>/dev/null || curl -s -X POST http://localhost:8080/api/v1/llm/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {
        "role": "user", 
        "content": "Hello, this is a test message"
      }
    ],
    "max_tokens": 50
  }'

echo ""
echo "📖 API Documentation available at: http://localhost:8080/swagger-ui.html"
echo "🏥 Health checks available at: http://localhost:8080/actuator/health"
echo ""
echo "🎉 Testing completed!"

# If we started the app, offer to stop it
if [ ! -z "$APP_PID" ]; then
    echo ""
    read -p "Stop the application? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        kill $APP_PID
        echo "🛑 Application stopped"
    else
        echo "📝 Application is still running with PID: $APP_PID"
        echo "   To stop it later, run: kill $APP_PID"
    fi
fi
