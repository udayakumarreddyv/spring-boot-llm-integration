# LLM Integration Frontend

This is a simple AngularJS-based web interface for interacting with the Spring Boot LLM Integration API.

## Features

- **Multi-Provider Support**: Select from available LLM providers (OpenAI, Anthropic, Ollama, etc.)
- **Real-time Chat Interface**: Interactive chat with LLM models
- **Provider Health Monitoring**: Check the status of all configured providers
- **Configurable Settings**: Adjust model, max tokens, and temperature
- **System Messages**: Add system prompts to guide the conversation
- **Chat Export**: Export conversation history as JSON
- **Error Handling**: Clear error messages and status indicators

## Setup

1. **Start the Spring Boot Application**:
   ```bash
   cd /path/to/spring-boot-llm-integration
   mvn spring-boot:run
   ```

2. **Serve the Frontend**:
   You can serve the frontend using any web server. Here are a few options:

   ### Option 1: Simple Python HTTP Server
   ```bash
   cd frontend
   python3 -m http.server 8081
   ```
   Then open: http://localhost:8081

   ### Option 2: Node.js http-server
   ```bash
   npm install -g http-server
   cd frontend
   http-server -p 8081 --cors
   ```
   Then open: http://localhost:8081

   ### Option 3: VS Code Live Server Extension
   - Install the "Live Server" extension in VS Code
   - Right-click on `index.html` and select "Open with Live Server"

## Usage

1. **Check Service Status**: The top of the interface shows the service health and number of available providers
2. **Select Provider**: Choose an LLM provider from the dropdown (or leave empty for default)
3. **Configure Settings**: 
   - **Model**: Specify a model name (optional, uses provider default)
   - **Max Tokens**: Set the maximum response length (1-4000)
   - **Temperature**: Control randomness (0.0 = deterministic, 2.0 = very random)
4. **Start Chatting**: Type your message and press Send or Ctrl+Enter
5. **System Messages**: Click "Add System Message" to set context or instructions
6. **Export**: Save your conversation as a JSON file

## API Endpoints Used

The frontend interacts with these Spring Boot API endpoints:

- `GET /api/v1/llm/health` - Check service health
- `GET /api/v1/llm/providers` - List available providers
- `GET /api/v1/llm/providers/{provider}/status` - Check provider status
- `POST /api/v1/llm/chat/completions` - Send chat completion requests

## CORS Configuration

If you encounter CORS issues, you may need to add CORS configuration to your Spring Boot application:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8081", "http://127.0.0.1:8081")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

## Troubleshooting

1. **Service Not Available**: Ensure the Spring Boot application is running on port 8080
2. **No Providers**: Check your `application.yml` configuration for provider settings
3. **CORS Errors**: Add the CORS configuration mentioned above
4. **Provider Errors**: Check that API keys and URLs are correctly configured in Spring Boot

## Customization

The frontend is built with:
- AngularJS 1.8.3
- Bootstrap 5.3.0
- Font Awesome icons

You can customize the styling by modifying the CSS in `index.html` or the functionality in `app.js`.
