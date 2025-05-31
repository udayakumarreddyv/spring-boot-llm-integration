package com.example.llm.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(LlmException.class)
    public ResponseEntity<ErrorResponse> handleLlmException(LlmException ex) {
        log.error("LLM exception occurred for provider {}: {}", ex.getProvider(), ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                ex.getProvider(),
                ex.getStatusCode(),
                LocalDateTime.now()
        );
        
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode());
        return ResponseEntity.status(status).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse error = new ErrorResponse(
                "Validation failed: " + errors.toString(),
                "validation",
                400,
                LocalDateTime.now()
        );
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument exception: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                "client",
                400,
                LocalDateTime.now()
        );
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
                "An unexpected error occurred",
                "server",
                500,
                LocalDateTime.now()
        );
        
        return ResponseEntity.internalServerError().body(error);
    }
    
    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
        private String source;
        private int statusCode;
        private LocalDateTime timestamp;
    }
}
