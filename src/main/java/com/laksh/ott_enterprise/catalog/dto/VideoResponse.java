package com.laksh.ott_enterprise.catalog.dto;

import com.laksh.ott_enterprise.catalog.enums.AccessLevel;
import com.laksh.ott_enterprise.catalog.enums.Genre;
import java.util.Set;

public record VideoResponse(
        Long id,
        String title,
        String description,
        String streamingUrl,
        String thumbnailUrl,
        Integer durationInSeconds,
        Genre genre,
        AccessLevel accessLevel,
        Set<ProductResponse> associatedProducts
) {}