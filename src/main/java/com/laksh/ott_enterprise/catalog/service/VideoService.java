package com.laksh.ott_enterprise.catalog.service;

import com.laksh.ott_enterprise.catalog.dto.ProductResponse;
import com.laksh.ott_enterprise.catalog.dto.VideoRequest;
import com.laksh.ott_enterprise.catalog.dto.VideoResponse;
import com.laksh.ott_enterprise.catalog.entity.ProductEntity;
import com.laksh.ott_enterprise.catalog.entity.VideoEntity;
import com.laksh.ott_enterprise.catalog.enums.Genre;
import com.laksh.ott_enterprise.catalog.repository.ProductRepository;
import com.laksh.ott_enterprise.catalog.repository.VideoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public VideoService(VideoRepository videoRepository, ProductRepository productRepository, ProductService productService) {
        this.videoRepository = videoRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Transactional
    @CacheEvict(value = "videos", allEntries = true)
    public VideoResponse createVideo(VideoRequest request) {
        Set<ProductEntity> products = new HashSet<>();
        if (request.productIds() != null && !request.productIds().isEmpty()) {
            List<ProductEntity> foundProducts = productRepository.findAllById(request.productIds());
            products.addAll(foundProducts);
        }

        VideoEntity entity = VideoEntity.builder()
                .title(request.title())
                .description(request.description())
                .streamingUrl(request.streamingUrl())
                .thumbnailUrl(request.thumbnailUrl())
                .durationInSeconds(request.durationInSeconds())
                .genre(request.genre())
                .accessLevel(request.accessLevel())
                .products(products)
                .build();

        VideoEntity saved = videoRepository.save(entity);
        return mapToResponse(saved);
    }

    @Cacheable(value = "videos", key = "#id")
    @Transactional(readOnly = true)
    public VideoResponse getVideoById(Long id) {
        VideoEntity entity = videoRepository.findWithProductsById(id)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));
        return mapToResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<VideoResponse> getAllVideos(Genre genre, Pageable pageable) {
        Page<VideoEntity> videoPage;
        if (genre != null) {
            videoPage = videoRepository.findByGenreWithProducts(genre, pageable);
        } else {
            videoPage = videoRepository.findAllWithProducts(pageable);
        }
        return videoPage.map(this::mapToResponse);
    }

    private VideoResponse mapToResponse(VideoEntity entity) {
        Set<ProductResponse> associatedProducts = entity.getProducts().stream()
                .map(productService::mapToResponse)
                .collect(Collectors.toSet());

        return new VideoResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStreamingUrl(),
                entity.getThumbnailUrl(),
                entity.getDurationInSeconds(),
                entity.getGenre(),
                entity.getAccessLevel(),
                associatedProducts
        );
    }
}
