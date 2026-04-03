package com.aishwary.URLShortener.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class UrlClick {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ipAddress;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "url_id")
    private Url url;

    public UrlClick(Url url, String ipAddress) {
        this.url = url;
        this.ipAddress = ipAddress;
        this.timestamp = LocalDateTime.now();
    }
}
