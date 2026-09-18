package com.laksh.ott_enterprise.catalog.repository;

import com.laksh.ott_enterprise.catalog.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findBySku(String sku);
    boolean existsBySku(String sku);
}
