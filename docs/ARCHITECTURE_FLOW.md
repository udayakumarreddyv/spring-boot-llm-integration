# Architecture Flow Diagram

## High-Level System Architecture

```mermaid
graph TB
    subgraph "Client Layer"
        C1[Web Browser/Postman]
        C2[curl/HTTP Client]
        C3[Mobile App]
        C4[Other Services]
    end

    subgraph "Spring Boot Application"
        subgraph "Controller Layer"
            LC[LlmController]
        end
        
        subgraph "Service Layer"
            LS[LlmService]
            HS[HealthService]
        end
        
        subgraph "Configuration Layer"
            LP[LlmProperties]
            WC[WebClientConfig]
        end
        
        subgraph "Exception Handling"
            GEH[GlobalExceptionHandler]
            CE[Custom Exceptions]
        end
    end

    subgraph "External LLM Providers"
        OAI[OpenAI API]
        ANT[Anthropic Claude]
        OLL[Ollama Local]
        AZ[Azure OpenAI]
        HF[HuggingFace]
    end

    subgraph "Monitoring & Documentation"
        SW[Swagger UI]
        ACT[Spring Actuator]
        LOG[Logging]
    end

    %% Client to Controller
    C1 --> LC
    C2 --> LC
    C3 --> LC
    C4 --> LC

    %% Controller to Services
    LC --> LS
    LC --> HS
    LC --> GEH

    %% Service to Configuration
    LS --> LP
    LS --> WC

    %% Service to External APIs
    LS --> OAI
    LS --> ANT
    LS --> OLL
    LS --> AZ
    LS --> HF

    %% Monitoring
    LC --> SW
    LC --> ACT
    LS --> LOG

    %% Exception Flow
    LS --> CE
    CE --> GEH
    GEH --> LC

    classDef clientStyle fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef springStyle fill:#e8f5e8,stroke:#2e7d32,stroke-width:2px
    classDef providerStyle fill:#fff3e0,stroke:#ef6c00,stroke-width:2px
    classDef monitorStyle fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px

    class C1,C2,C3,C4 clientStyle
    class LC,LS,HS,LP,WC,GEH,CE springStyle
    class OAI,ANT,OLL,AZ,HF providerStyle
    class SW,ACT,LOG monitorStyle
```

## Request Flow Diagram

```mermaid
sequenceDiagram
    participant Client
    participant Controller as LlmController
    participant Service as LlmService
    participant Config as LlmProperties
    participant Provider as LLM Provider
    participant Health as HealthService

    Note over Client,Health: Chat Completion Flow
    
    Client->>Controller: POST /api/v1/llm/chat/completions
    Controller->>Controller: Validate Request
    Controller->>Service: processChatCompletion(request)
    
    Service->>Config: getProviderConfig(provider)
    Config-->>Service: ProviderConfig
    
    Service->>Service: buildWebClient()
    Service->>Service: prepareHeaders(provider)
    Service->>Service: buildRequestBody(request)
    
    Service->>Provider: HTTP POST /chat/completions
    
    alt Success Response
        Provider-->>Service: ChatResponse
        Service-->>Controller: ChatResponse
        Controller-->>Client: 200 OK + Response
    else Error Response
        Provider-->>Service: Error
        Service->>Service: handleProviderError()
        Service-->>Controller: Custom Exception
        Controller-->>Client: Error Response
    end

    Note over Client,Health: Health Check Flow
    
    Client->>Controller: GET /api/v1/llm/health
    Controller->>Health: checkHealth()
    
    loop For each enabled provider
        Health->>Provider: Health Check Request
        Provider-->>Health: Status Response
    end
    
    Health-->>Controller: Health Summary
    Controller-->>Client: Health Status
```

## Component Interaction Flow

```mermaid
flowchart TD
    Start([Client Request]) --> Validate{Validate Request}
    
    Validate -->|Valid| Route{Route Request}
    Validate -->|Invalid| Error1[Return Validation Error]
    
    Route -->|Chat Completion| Chat[Process Chat Request]
    Route -->|Health Check| Health[Check Provider Health]
    Route -->|Get Providers| Providers[List Available Providers]
    
    Chat --> SelectProvider{Select Provider}
    SelectProvider --> ConfigCheck{Provider Configured?}
    
    ConfigCheck -->|Yes| BuildClient[Build WebClient]
    ConfigCheck -->|No| Error2[Provider Not Available Error]
    
    BuildClient --> PrepareRequest[Prepare Request Headers & Body]
    PrepareRequest --> CallAPI[Call Provider API]
    
    CallAPI --> APIResponse{API Response}
    APIResponse -->|Success| Transform[Transform Response]
    APIResponse -->|Error| HandleError[Handle Provider Error]
    
    Transform --> Success[Return Success Response]
    HandleError --> Error3[Return Error Response]
    
    Health --> CheckAll[Check All Providers]
    CheckAll --> HealthSummary[Build Health Summary]
    HealthSummary --> Success
    
    Providers --> ListEnabled[List Enabled Providers]
    ListEnabled --> Success
    
    Success --> End([Response to Client])
    Error1 --> End
    Error2 --> End
    Error3 --> End

    classDef startEnd fill:#4caf50,stroke:#2e7d32,stroke-width:2px,color:#fff
    classDef process fill:#2196f3,stroke:#1976d2,stroke-width:2px,color:#fff
    classDef decision fill:#ff9800,stroke:#f57c00,stroke-width:2px,color:#fff
    classDef error fill:#f44336,stroke:#d32f2f,stroke-width:2px,color:#fff

    class Start,End startEnd
    class Chat,Health,Providers,BuildClient,PrepareRequest,CallAPI,Transform,CheckAll,HealthSummary,ListEnabled process
    class Validate,Route,SelectProvider,ConfigCheck,APIResponse decision
    class Error1,Error2,Error3,HandleError error
```

## Data Flow Architecture

```mermaid
graph LR
    subgraph "Input Layer"
        REQ[HTTP Request]
        JSON[JSON Payload]
    end

    subgraph "Processing Layer"
        VALID[Validation]
        TRANS[Transformation]
        ROUTE[Routing Logic]
    end

    subgraph "Business Layer"
        BL[Business Logic]
        PSEL[Provider Selection]
        CONF[Configuration Loading]
    end

    subgraph "Integration Layer"
        WC[WebClient]
        AUTH[Authentication]
        HDR[Header Management]
    end

    subgraph "External Layer"
        API1[OpenAI API]
        API2[Anthropic API]
        API3[Ollama API]
        API4[Azure OpenAI API]
        API5[HuggingFace API]
    end

    subgraph "Output Layer"
        RESP[HTTP Response]
        ERR[Error Response]
        HEALTH[Health Status]
    end

    REQ --> VALID
    JSON --> VALID
    VALID --> TRANS
    TRANS --> ROUTE
    ROUTE --> BL
    BL --> PSEL
    PSEL --> CONF
    CONF --> WC
    WC --> AUTH
    AUTH --> HDR
    HDR --> API1
    HDR --> API2
    HDR --> API3
    HDR --> API4
    HDR --> API5
    
    API1 --> RESP
    API2 --> RESP
    API3 --> RESP
    API4 --> RESP
    API5 --> RESP
    
    BL --> ERR
    BL --> HEALTH
    ERR --> RESP
    HEALTH --> RESP

    classDef inputStyle fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef processStyle fill:#e8f5e8,stroke:#388e3c,stroke-width:2px
    classDef businessStyle fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef integrationStyle fill:#fce4ec,stroke:#c2185b,stroke-width:2px
    classDef externalStyle fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    classDef outputStyle fill:#e0f2f1,stroke:#00695c,stroke-width:2px

    class REQ,JSON inputStyle
    class VALID,TRANS,ROUTE processStyle
    class BL,PSEL,CONF businessStyle
    class WC,AUTH,HDR integrationStyle
    class API1,API2,API3,API4,API5 externalStyle
    class RESP,ERR,HEALTH outputStyle
```

## Error Handling Flow

```mermaid
flowchart TD
    Request[Incoming Request] --> Try{Try Processing}
    
    Try -->|Success| Success[Return Response]
    Try -->|Exception| CatchType{Exception Type}
    
    CatchType -->|Validation Error| ValError[ValidationException]
    CatchType -->|Provider Error| ProvError[ProviderException]
    CatchType -->|Network Error| NetError[NetworkException]
    CatchType -->|Generic Error| GenError[Generic Exception]
    
    ValError --> GEH[GlobalExceptionHandler]
    ProvError --> GEH
    NetError --> GEH
    GenError --> GEH
    
    GEH --> BuildError[Build Error Response]
    BuildError --> LogError[Log Error Details]
    LogError --> ErrorResponse[Return Error Response]
    
    Success --> End[End]
    ErrorResponse --> End

    classDef successStyle fill:#4caf50,stroke:#2e7d32,stroke-width:2px,color:#fff
    classDef errorStyle fill:#f44336,stroke:#d32f2f,stroke-width:2px,color:#fff
    classDef processStyle fill:#2196f3,stroke:#1976d2,stroke-width:2px,color:#fff
    classDef decisionStyle fill:#ff9800,stroke:#f57c00,stroke-width:2px,color:#fff

    class Request,Success,End successStyle
    class ValError,ProvError,NetError,GenError,BuildError,LogError,ErrorResponse errorStyle
    class GEH processStyle
    class Try,CatchType decisionStyle
```

## Configuration Management Flow

```mermaid
graph TB
    subgraph "Configuration Sources"
        YML[application.yml]
        ENV[Environment Variables]
        PROP[System Properties]
    end

    subgraph "Spring Configuration"
        CP[@ConfigurationProperties]
        VAL[@Validated]
        BIND[Property Binding]
    end

    subgraph "LLM Configuration"
        LP[LlmProperties]
        PC[ProviderConfig]
        DC[Default Config]
    end

    subgraph "Runtime Usage"
        SVC[LlmService]
        WC[WebClient]
        API[API Calls]
    end

    YML --> BIND
    ENV --> BIND
    PROP --> BIND
    
    BIND --> CP
    CP --> VAL
    VAL --> LP
    
    LP --> PC
    LP --> DC
    
    PC --> SVC
    DC --> SVC
    SVC --> WC
    WC --> API

    classDef sourceStyle fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef springStyle fill:#e8f5e8,stroke:#388e3c,stroke-width:2px
    classDef configStyle fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef runtimeStyle fill:#fce4ec,stroke:#c2185b,stroke-width:2px

    class YML,ENV,PROP sourceStyle
    class CP,VAL,BIND springStyle
    class LP,PC,DC configStyle
    class SVC,WC,API runtimeStyle
```
