package com.example.manual.exception;

import java.time.LocalDateTime;
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
  //#region 
  //#endregion
