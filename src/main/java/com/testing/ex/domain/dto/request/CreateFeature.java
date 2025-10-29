package com.testing.ex.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateFeature(
        @NotBlank String ram,
        @NotBlank String storage,
        @NotBlank String battery
) {
}
