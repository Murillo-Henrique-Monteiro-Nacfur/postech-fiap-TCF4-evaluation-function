package com.postech.fiap.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDateTime;

@RegisterForReflection
public class ErrorDTO {
    private String message;
    private String code;
    private LocalDateTime timestamp;

    public ErrorDTO() {
    }

    public ErrorDTO(String message, String code) {
        this.message = message;
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}