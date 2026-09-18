package com.laksh.ott_enterprise.catalog.service;

import com.laksh.ott_enterprise.catalog.dto.ProductRequest;
import com.laksh.ott_enterprise.catalog.dto.ProductResponse;
import com.laksh.ott_enterprise.catalog.entity.ProductEntity;
import com.laksh.ott_enterprise.catalog.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @CacheEvict(value = {"videos", "products"}, allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new IllegalArgumentException("Product with SKU " + request.sku() + " already exists");
        }
        ProductEntity entity = ProductEntity.builder()
                .name(request.name())
                .description(request.description())
                .sku(request.sku())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .imageUrl(request.imageUrl())
                .build();

        ProductEntity savedEntity = productRepository.save(entity);
        return mapToResponse(savedEntity);
    }

    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return mapToResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductResponse mapToResponse(ProductEntity entity) {
        return new ProductResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getSku(),
                entity.getPrice(),
                entity.getStockQuantity(),
                entity.getImageUrl()
        );
    }
}
