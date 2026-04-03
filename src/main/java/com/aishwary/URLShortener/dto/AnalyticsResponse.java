package com.aishwary.URLShortener.dto;

import java.time.LocalDateTime;

public class AnalyticsResponse {
    private String originalUrl;
    private String shortCode;
    private int clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessed;

    public AnalyticsResponse(String originalUrl, String shortCode, int clickCount, LocalDateTime createdAt, LocalDateTime lastAccessed) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.clickCount = clickCount;
        this.createdAt = createdAt;
        this.lastAccessed = lastAccessed;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public int getClickCount() {
        return clickCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastAccessed() {
        return lastAccessed;
    }
}
