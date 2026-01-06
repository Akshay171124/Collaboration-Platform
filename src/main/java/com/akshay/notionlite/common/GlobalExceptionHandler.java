package com.akshay.notionlite.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    record ErrorRes(String error, String message, int status, String timestamp) {}

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorRes> handle(ApiException ex) {
        var body = new ErrorRes(
                ex.getStatus().getReasonPhrase(),
                ex.getMessage(),
                ex.getStatus().value(),
                Instant.now().toString()
        );
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRes> handleOther(Exception ex) {
        var body = new ErrorRes("Internal Server Error", "Unexpected error", 500, Instant.now().toString());
        return ResponseEntity.status(500).body(body);
    }
}
