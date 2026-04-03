package com.aishwary.URLShortener.repository;

import com.aishwary.URLShortener.model.UrlClick;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlClickRepository extends JpaRepository<UrlClick, Long> {
}
