package com.aishwary.URLShortener.service;

import com.aishwary.URLShortener.model.Url;
import com.aishwary.URLShortener.repository.UrlRepository;
import com.aishwary.URLShortener.util.ShortCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepo;

    public String createShortUrl(String originalUrl) {
        String shortCode = ShortCodeGenerator.generateCode();
        Url url = new Url(originalUrl, shortCode);
        urlRepo.save(url);
        return shortCode;
    }

    public Url getOriginalUrl(String shortCode) {
        Url url = urlRepo.findByShortCode(shortCode).orElseThrow(() -> new RuntimeException("URL not found"));
        url.setClickCount(url.getClickCount() + 1);
        urlRepo.save(url);
        return url;
    }
}
