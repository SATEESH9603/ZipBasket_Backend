package com.example.onlinetest.Domain.Dto;

import java.util.List;

import com.example.onlinetest.Domain.ErrorModel;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response containing paginated list of products")
public class ProductsListResponseDto {
    
    @Schema(description = "Indicates if the operation was successful", example = "true")
    private boolean success;
    
    @Schema(description = "Response message", example = "Products retrieved successfully")
    private String message;
    
    @Schema(description = "Error details if the operation failed")
    private ErrorModel error;
    
    @ArraySchema(
        schema = @Schema(implementation = ProductDto.class),
        minItems = 0,
        maxItems = 5,
        uniqueItems = true
    )
    @Schema(description = "List of products in the current page")
    private List<ProductDto> products;
    
    @Schema(description = "Current page number", example = "1")
    private int page;
    
    @Schema(description = "Total number of pages", example = "10")
    private int totalPages;
    
    @Schema(description = "Total number of products across all pages", example = "47")
    private long totalItems;
}
