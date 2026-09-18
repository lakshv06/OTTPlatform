package com.laksh.ott_enterprise.catalog.repository;

import com.laksh.ott_enterprise.catalog.entity.VideoEntity;
import com.laksh.ott_enterprise.catalog.enums.Genre;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {

    @EntityGraph(attributePaths = {"products"})
    Optional<VideoEntity> findWithProductsById(Long id);

    @Query(value = "SELECT DISTINCT v FROM VideoEntity v LEFT JOIN FETCH v.products",
            countQuery = "SELECT count(v) FROM VideoEntity v"
    )
    Page<VideoEntity> findAllWithProducts(Pageable pageable);

    @Query(value = "SELECT DISTINCT v FROM VideoEntity v LEFT JOIN FETCH v.products WHERE v.genre = :genre",
            countQuery = "SELECT count(v) FROM VideoEntity v WHERE v.genre = :genre")
    Page<VideoEntity> findByGenreWithProducts(@Param("genre") Genre genre, Pageable pageable);
}
