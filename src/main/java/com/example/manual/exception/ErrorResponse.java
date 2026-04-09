package com.example.manual.exception;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    private String message;
    private String errorCode;
    private LocalDateTime timestamp;

    public String getMessage() {
        return this.message;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }
}
