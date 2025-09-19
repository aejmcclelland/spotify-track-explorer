package com.amcclelland.ste_server.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "spotify_accounts",
    indexes = {
        @Index(name = "idx_spotify_accounts_spotify_user_id", columnList = "spotify_user_id")
    }
)
public class SpotifyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "spotify_user_id", nullable = false, length = 64)
    private String spotifyUserId;

    @Column(name = "display_name", length = 255)
    private String displayName;

    @Column(name = "refresh_token", nullable = false, columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "scope", columnDefinition = "TEXT")
    private String scope;

    @Column(name = "token_obtained_at", nullable = false)
    private Instant tokenObtainedAt;

    @Column(name = "expires_in_seconds")
    private Integer expiresInSeconds;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected SpotifyAccount() {}

    public SpotifyAccount(Long userId,
                          String spotifyUserId,
                          String displayName,
                          String refreshToken,
                          String scope,
                          Integer expiresInSeconds) {
        this.userId = userId;
        this.spotifyUserId = spotifyUserId;
        this.displayName = displayName;
        this.refreshToken = refreshToken;
        this.scope = scope;
        this.expiresInSeconds = expiresInSeconds;
        this.tokenObtainedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (this.tokenObtainedAt == null) this.tokenObtainedAt = now;
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Getters & setters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSpotifyUserId() { return spotifyUserId; }
    public void setSpotifyUserId(String spotifyUserId) { this.spotifyUserId = spotifyUserId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public Instant getTokenObtainedAt() { return tokenObtainedAt; }
    public void setTokenObtainedAt(Instant tokenObtainedAt) { this.tokenObtainedAt = tokenObtainedAt; }

    public Integer getExpiresInSeconds() { return expiresInSeconds; }
    public void setExpiresInSeconds(Integer expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
