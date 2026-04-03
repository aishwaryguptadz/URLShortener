package com.aishwary.URLShortener.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String originalUrl;

    @Column(unique = true)
    private String shortCode;
    private int clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessed;

    public Url(String originalUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.clickCount = 0;
        this.createdAt = LocalDateTime.now();
    }
}