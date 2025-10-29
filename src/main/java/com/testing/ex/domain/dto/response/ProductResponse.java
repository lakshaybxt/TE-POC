package com.testing.ex.domain.dto.response;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String sku,
        String category,
        BigDecimal price,
        FeatureResponse response,
        String description
) {
}