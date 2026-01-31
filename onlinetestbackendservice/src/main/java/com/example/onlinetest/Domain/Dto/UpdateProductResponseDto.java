package com.example.onlinetest.Domain.Dto;

import com.example.onlinetest.Domain.ErrorModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response containing Updated product details")
public class UpdateProductResponseDto {
    @Schema(description = "Indicates if the operation was successful", example = "true")
    private boolean success;
    
    @Schema(description = "Response message", example = "Products retrieved successfully")
    private String message;
    
    @Schema(description = "Error details if the operation failed")
    private ErrorModel error;
    
    @Schema(description = "product details")
    private ProductDto product;
}
