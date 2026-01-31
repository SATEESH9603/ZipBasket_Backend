package com.example.onlinetest.Domain.Dto;

import com.example.onlinetest.Domain.ErrorModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response for create product operation")
public class CreateProductResponseDto {
    @Schema(description = "Indicates if the operation was successful", example = "true")
    private boolean success;

    @Schema(description = "Response message", example = "Product created successfully")
    private String message;

    @Schema(description = "Error details if the operation failed")
    private ErrorModel error;

    @Schema(description = "Created product data")
    private ProductDto product;
}
