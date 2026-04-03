package com.aishwary.URLShortener.service;

import com.aishwary.URLShortener.exception.UrlNotFoundException;
import com.aishwary.URLShortener.model.Url;
import com.aishwary.URLShortener.model.UrlClick;
import com.aishwary.URLShortener.repository.UrlClickRepository;
import com.aishwary.URLShortener.repository.UrlRepository;
import com.aishwary.URLShortener.util.ShortCodeGenerator;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.InvalidUrlException;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlService {
    private final UrlRepository urlRepo;
    private final UrlClickRepository urlClickRepo;

    public UrlService(UrlRepository urlRepo, UrlClickRepository urlClickRepo) {
        this.urlRepo = urlRepo;
        this.urlClickRepo = urlClickRepo;
    }

    public String createShortUrl(String originalUrl) {
        originalUrl = normalizeUrl(originalUrl);
        validateUrl(originalUrl);
        Optional<Url> existing = urlRepo.findByOriginalUrl(originalUrl);
        if (existing.isPresent()) return existing.get().getShortCode();
        String shortCode;
        do {
            shortCode = ShortCodeGenerator.generateCode();
        } while (urlRepo.findByShortCode(shortCode).isPresent());
        Url url = new Url(originalUrl, shortCode);
        urlRepo.save(url);
        return shortCode;
    }

    @Cacheable(value = "urls", key = "#shortCode")
    public Url getUrlFromCache(String shortCode) {
        return urlRepo.findByShortCode(shortCode).orElseThrow(() -> new UrlNotFoundException("Short URL not found"));
    }

    public Url getOriginalUrl(String shortCode, String ipAddress) {
        Url url = getUrlFromCache(shortCode);
        url.setClickCount(url.getClickCount() + 1);
        url.setLastAccessed(LocalDateTime.now());
        urlRepo.save(url);
        UrlClick click = new UrlClick(url, ipAddress);
        urlClickRepo.save(click);
        return url;
    }

    public Url getAnalytics(String shortCode) {
        return getUrlFromCache(shortCode);
    }

    private void validateUrl(String url) {
        try {
            new URL(url).toURI();
        } catch (Exception e) {
            throw new InvalidUrlException("Invalid URL format");
        }
    }

    private String normalizeUrl(String url) {
        url = url.trim();
        if (!url.startsWith("https://") && !url.startsWith("http://")) url = "https://" + url;
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        return url;
    }

    @CachePut(value = "urls", key = "#shortCode")
    public Url updateClickStats(String shortCode, String ipAddress) {
        Url url = urlRepo.findByShortCode(shortCode).orElseThrow(() -> new UrlNotFoundException("Short URL not found"));
        url.setClickCount(url.getClickCount() + 1);
        url.setLastAccessed(LocalDateTime.now());
        urlRepo.save(url);
        UrlClick click = new UrlClick(url, ipAddress);
        urlClickRepo.save(click);
        return url;
    }
}