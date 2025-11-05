package com.example.onlinetest.Domain;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "Error details when an operation fails")
public class ErrorModel {
    
    @Schema(
        description = "User-friendly error message",
        example = "Invalid request parameters"
    )
    public String message;
    
    @Schema(
        description = "Unique error code for this type of error",
        example = "INVALID_INPUT"
    )
    public String errorCode;
    
    @Schema(
        description = "Technical error message for developers",
        example = "Parameter 'page' must be greater than 0"
    )
    public String developerMessage;
}
