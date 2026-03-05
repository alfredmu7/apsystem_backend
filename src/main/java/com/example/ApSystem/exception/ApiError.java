package com.example.ApSystem.exception;

import lombok.*;
import java.time.Instant;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ApiError {
    private Instant timestamp = Instant.now();
    private int status;
    private String error;
    private String message;
    private String path;

    public ApiError(int value, String message) {
    }
}
