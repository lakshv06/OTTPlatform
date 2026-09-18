package com.laksh.ott_enterprise.catalog.controller;

import com.laksh.ott_enterprise.catalog.dto.VideoRequest;
import com.laksh.ott_enterprise.catalog.dto.VideoResponse;
import com.laksh.ott_enterprise.catalog.enums.Genre;
import com.laksh.ott_enterprise.catalog.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoResponse> createVideo(@Valid @RequestBody VideoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(videoService.createVideo(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoResponse> getVideoById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getVideoById(id));
    }

    @GetMapping
    public ResponseEntity<Page<VideoResponse>> getAllVideos(
            @RequestParam(required = false) Genre genre,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(videoService.getAllVideos(genre, pageable));
    }
}