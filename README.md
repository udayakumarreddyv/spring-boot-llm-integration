# Spring Boot LLM Integration

A comprehensive Spring Boot application for integrating with multiple OpenAI-compatible LLM providers including OpenAI, Anthropic Claude, Ollama, Azure OpenAI, and HuggingFace.

## 📚 Documentation

- **[📖 Documentation Index](docs/README.md)** - Complete documentation overview and navigation
- **[🏗️ Architecture Flow Diagram](docs/ARCHITECTURE_FLOW.md)** - Visual system architecture and flow diagrams
- **[🛠️ API Usage Guide](docs/API_USAGE_GUIDE.md)** - Comprehensive guide for using the API endpoints
- **[📊 Project Summary](docs/PROJECT_SUMMARY.md)** - Complete project overview and technical details
- **[🔗 API Documentation](http://localhost:8080/swagger-ui.html)** - Interactive Swagger UI (when application is running)

> 💡 **New to the project?** Start with the [Documentation Index](docs/README.md) for guided navigation.

## ✨ Features

- **Multi-Provider Support**: Seamlessly integrate with various LLM providers using OpenAI-compatible APIs
- **Reactive Architecture**: Built with Spring WebFlux for non-blocking, reactive HTTP operations
- **Configuration-Driven**: Easy provider configuration through YAML properties
- **Health Monitoring**: Built-in health checks and provider availability status
- **Error Handling**: Comprehensive error handling with detailed error responses
- **API Documentation**: Auto-generated OpenAPI documentation with Swagger UI
- **Testing**: Comprehensive unit tests with MockWebServer

## 🔌 Supported Providers

| Provider | Status | Authentication | Base URL | Default Model |
|----------|--------|---------------|----------|---------------|
| OpenAI | ✅ | Bearer Token | https://api.openai.com/v1 | gpt-3.5-turbo |
| Anthropic Claude | ✅ | x-api-key | https://api.anthropic.com/v1 | claude-3-sonnet-20240229 |
| Ollama | ✅ | Bearer Token | http://localhost:11434/v1 | llama2 |
| Azure OpenAI | ✅ | Bearer Token | Custom endpoint | gpt-35-turbo |
| HuggingFace | ✅ | Bearer Token | https://api-inference.huggingface.co/models | microsoft/DialoGPT-medium |

## 🚀 Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6 or higher

### Installation & Setup

1. **Clone the repository:**
```bash
git clone <repository-url>
cd spring-boot-llm-integration
```

2. **Configure your API keys** in `application.yml` or as environment variables:
```bash
export OPENAI_API_KEY="your-openai-api-key"
export ANTHROPIC_API_KEY="your-anthropic-api-key"
export HUGGINGFACE_API_KEY="your-huggingface-api-key"
```

3. **Build and run the application:**
```bash
mvn spring-boot:run
```

Or use the provided test script:
```bash
./test-api.sh
```

4. **Access the application:**
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Health Check: http://localhost:8080/actuator/health
   - LLM Health: http://localhost:8080/api/v1/llm/health

### First API Call

Test the chat completion endpoint:
```bash
curl -X POST http://localhost:8080/api/v1/llm/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {"role": "user", "content": "Hello, how are you?"}
    ],
    "provider": "ollama"
  }'
```

> 📖 **For detailed API usage examples, see the [API Usage Guide](docs/API_USAGE_GUIDE.md)**

## ⚙️ Configuration

### Provider Configuration

Configure providers in `src/main/resources/application.yml`:

```yaml
llm:
  providers:
    openai:
      enabled: true
      base-url: "https://api.openai.com/v1"
      api-key: "${OPENAI_API_KEY:}"
      default-model: "gpt-3.5-turbo"
      timeout: 30000
    
    anthropic:
      enabled: true
      base-url: "https://api.anthropic.com/v1"
      api-key: "${ANTHROPIC_API_KEY:}"
      default-model: "claude-3-sonnet-20240229"
      timeout: 30000
    
    ollama:
      enabled: true
      base-url: "http://localhost:11434/v1"
      api-key: "ollama"
      default-model: "llama2"
      timeout: 60000

  default-provider: "openai"
  max-tokens: 1000
  temperature: 0.7
  stream: false
```

### Environment Variables

For production deployment, use environment variables:
```bash
export OPENAI_API_KEY="sk-..."
export ANTHROPIC_API_KEY="sk-ant-api03-..."
export AZURE_OPENAI_API_KEY="..."
export AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com"
export HUGGINGFACE_API_KEY="hf_..."
```

## 🛠️ API Endpoints

### Core Endpoints

| Endpoint | Method | Description |
|----------|---------|-------------|
| `/api/v1/llm/chat/completions` | POST | Chat completion with any provider |
| `/api/v1/llm/health` | GET | Service health and provider status |
| `/api/v1/llm/providers` | GET | List available providers |
| `/actuator/health` | GET | Application health check |

### Quick Examples

**Chat Completion:**
```bash
POST /api/v1/llm/chat/completions
Content-Type: application/json

{
  "messages": [
    {"role": "user", "content": "Explain quantum computing"}
  ],
  "provider": "openai",
  "max_tokens": 500,
  "temperature": 0.7
}
```

**Health Check:**
```bash
GET /api/v1/llm/health

Response:
{
  "status": "UP",
  "availableProviders": 3,
  "providers": ["openai", "anthropic", "ollama"]
}
```

> 📖 **For complete API documentation with all parameters and examples, see the [API Usage Guide](docs/API_USAGE_GUIDE.md)**

## 🏗️ Architecture

### Project Structure
```
src/main/java/com/example/llm/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data transfer objects
├── exception/      # Custom exceptions
├── model/          # Domain models
└── service/        # Business logic services
```

### Key Components
- **LlmService**: Core service for LLM operations
- **WebClientConfig**: HTTP client configuration
- **LlmProperties**: Configuration properties binding
- **GlobalExceptionHandler**: Centralized error handling

## 🧪 Testing

Run all tests:
```bash
mvn test
```

Run with coverage:
```bash
mvn test jacoco:report
```

### Test Structure
- **Unit Tests**: Service layer testing with MockWebServer
- **Integration Tests**: Full application context testing
- **Reactive Testing**: StepVerifier for reactive streams
        "content": "Write a haiku about spring"
      }
    ],
    "max_tokens": 100
  }'
```

### Check Available Providers

```bash
curl http://localhost:8080/api/v1/llm/providers
```

## 📊 API Documentation

Once the application is running, you can access the interactive API documentation at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 📈 Monitoring

### Health Endpoints
- **Application health**: http://localhost:8080/actuator/health
- **Service metrics**: http://localhost:8080/actuator/metrics
- **Application info**: http://localhost:8080/actuator/info

### Custom Health Checks
- **LLM Service Health**: http://localhost:8080/api/v1/llm/health
- **Provider Status**: Individual provider availability and configuration

## 🚀 Deployment

### Development
```bash
# Run in development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
mvn test

# Run specific test class
mvn test -Dtest=LlmServiceImplTest

# Run with coverage
mvn test jacoco:report
```

### Production
```bash
# Create executable JAR
mvn clean package

# Run the JAR
java -jar target/spring-boot-llm-integration-1.0.0.jar

# Run with production profile
java -jar -Dspring.profiles.active=prod target/spring-boot-llm-integration-1.0.0.jar
```

### Docker (Optional)
```bash
# Build Docker image
docker build -t spring-boot-llm-integration .

# Run container
docker run -p 8080:8080 \
  -e OPENAI_API_KEY="your-key" \
  -e ANTHROPIC_API_KEY="your-key" \
  spring-boot-llm-integration
```

## ⚠️ Error Handling

The application provides consistent error responses across all endpoints:

```json
{
  "message": "Provider 'invalid-provider' is not available or not configured",
  "source": "invalid-provider", 
  "statusCode": 400,
  "timestamp": "2025-05-28T10:30:00"
}
```

### Common Error Scenarios
- **400 Bad Request**: Invalid provider or malformed request
- **401 Unauthorized**: Missing or invalid API keys
- **429 Too Many Requests**: Rate limiting from provider
- **503 Service Unavailable**: Provider is down or unreachable
- **504 Gateway Timeout**: Request timeout exceeded

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Add tests**: Ensure your changes are well tested
4. **Commit your changes**: `git commit -m 'Add some amazing feature'`
5. **Push to the branch**: `git push origin feature/amazing-feature`
6. **Open a Pull Request**

### Development Guidelines
- Follow Spring Boot best practices
- Use reactive programming patterns
- Write comprehensive unit tests
- Update documentation for new features
- Ensure backward compatibility

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🔧 Troubleshooting

### Common Issues

| Issue | Cause | Solution |
|-------|-------|----------|
| **Provider not available** | Missing API keys or network issues | Check API keys and network connectivity |
| **Timeout errors** | Long response times | Increase timeout values in configuration |
| **Model not found** | Invalid model names | Verify model names for each provider |
| **Rate limiting** | Too many requests | Implement retry logic or reduce frequency |
| **Connection refused** | Service not running | Ensure provider services are accessible |

### Debug Configuration

Enable detailed logging for troubleshooting:

```yaml
logging:
  level:
    com.example.llm: DEBUG
    org.springframework.web.reactive.function.client: DEBUG
    reactor.netty.http.client: DEBUG
```

### Health Check Commands

Quick commands to verify system health:
```bash
# Check application health
curl http://localhost:8080/actuator/health

# Check LLM service health
curl http://localhost:8080/api/v1/llm/health

# List available providers
curl http://localhost:8080/api/v1/llm/providers

# Test a simple chat completion
curl -X POST http://localhost:8080/api/v1/llm/chat/completions \
  -H "Content-Type: application/json" \
  -d '{"messages":[{"role":"user","content":"Hello"}],"provider":"ollama"}'
```

### Support

- **Issues**: Report bugs and feature requests on GitHub Issues
- **Documentation**: Check the [API Usage Guide](docs/API_USAGE_GUIDE.md) for detailed examples
- **Project Status**: See [Project Summary](docs/PROJECT_SUMMARY.md) for current implementation status
