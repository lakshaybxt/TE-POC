package com.testing.ex.domain.dto.request;


import com.testing.ex.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProduct(
        @NotBlank String name,
        @NotBlank String sku,
        @NotNull BigDecimal price,
        String description,
        @NotNull Category category,
        @NotNull CreateFeature features
) {}