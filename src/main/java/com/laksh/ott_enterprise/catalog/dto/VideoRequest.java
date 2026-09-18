package com.laksh.ott_enterprise.catalog.dto;

import com.laksh.ott_enterprise.catalog.enums.AccessLevel;
import com.laksh.ott_enterprise.catalog.enums.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record VideoRequest(
        @NotBlank(message = "Title is required") String title,
        String description,
        @NotBlank(message = "Streaming URL is required") String streamingUrl,
        String thumbnailUrl,
        Integer durationInSeconds,
        @NotNull(message = "Genre is required") Genre genre,
        @NotNull(message = "Access level is required") AccessLevel accessLevel,
        Set<Long> productIds
) {
}
