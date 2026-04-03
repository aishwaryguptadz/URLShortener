package com.aishwary.URLShortener.controller;

import com.aishwary.URLShortener.dto.AnalyticsResponse;
import com.aishwary.URLShortener.dto.UrlRequest;
import com.aishwary.URLShortener.dto.UrlResponse;
import com.aishwary.URLShortener.model.Url;
import com.aishwary.URLShortener.service.RateLimitService;
import com.aishwary.URLShortener.service.UrlService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {
    @Autowired
    private UrlService urlService;
    @Autowired
    private RateLimitService rateLimitService;

    @PostMapping("/api/shorten")
    public ResponseEntity<?> createShortUrl(@RequestBody UrlRequest request, HttpServletRequest httpServletRequest) {
        String ip = httpServletRequest.getRemoteAddr();
        Bucket bucket = rateLimitService.resolveBucket(ip);
        if (!bucket.tryConsume(1)) return ResponseEntity.status(429).body("Too many requests. Try again later.");
        String code = urlService.createShortUrl(request.getOriginalUrl());
        String shortUrl = "http://localhost:8080/" + code;
        return ResponseEntity.ok(new UrlResponse(shortUrl));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        Url url = urlService.updateClickStats(shortCode, ipAddress);
        return ResponseEntity.status(302).location(URI.create(url.getOriginalUrl())).build();
    }

    @GetMapping("/api/analytics/{shortCode}")
    public AnalyticsResponse getAnalytics(@PathVariable String shortCode) {
        Url url = urlService.getAnalytics(shortCode);
        return new AnalyticsResponse(
                url.getOriginalUrl(),
                url.getShortCode(),
                url.getClickCount(),
                url.getCreatedAt(),
                url.getLastAccessed()
        );
    }
}