package com.testing.ex.domain.dto.request;

import com.testing.ex.domain.Category;

import java.math.BigDecimal;

public record UpdateProduct(
        String name,
        String sku,
        BigDecimal price,
        String description,
        Category category,
        UpdateFeature features
) {
}
