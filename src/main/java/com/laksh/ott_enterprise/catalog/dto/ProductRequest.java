package com.laksh.ott_enterprise.catalog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Product name is required") String name,
        String description,
        @NotBlank(message = "SKU is required") String sku,
        @NotNull(message = "Price is required") @Min(0) BigDecimal price,
        @NotNull(message = "Stock quantity is required") @Min(0) Integer stockQuantity,
        String imageUrl
) {
}
