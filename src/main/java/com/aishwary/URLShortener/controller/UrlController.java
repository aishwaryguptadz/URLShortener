package com.aishwary.URLShortener.controller;

import com.aishwary.URLShortener.dto.UrlRequest;
import com.aishwary.URLShortener.dto.UrlResponse;
import com.aishwary.URLShortener.model.Url;
import com.aishwary.URLShortener.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {
    @Autowired
    private UrlService urlService;

    @PostMapping("/api/shorten")
    public UrlResponse createShortUrl(@RequestBody UrlRequest request) {
        String code = urlService.createShortUrl(request.getOriginalUrl());
        String shortUrl = "http://localhost:8080/" + code;
        return new UrlResponse(shortUrl);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode) {
        Url url = urlService.getOriginalUrl(shortCode);
        return ResponseEntity.status(302).location(URI.create(url.getOriginalUrl())).build();
    }
}