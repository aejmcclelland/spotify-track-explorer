package com.amcclelland.ste_server.application;

import com.amcclelland.ste_server.domain.SpotifyAccount;
import com.amcclelland.ste_server.infra.SpotifyAccountRepository;
import com.amcclelland.ste_server.infra.SpotifyClient;
import com.amcclelland.ste_server.infra.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SpotifyService {

    private final UserRepository users;
    private final SpotifyAccountRepository accounts;
    private final SpotifyClient spotifyClient;

    public SpotifyService(UserRepository users,
            SpotifyAccountRepository accounts,
            SpotifyClient spotifyClient) {
        this.users = users;
        this.accounts = accounts;
        this.spotifyClient = spotifyClient;
    }

    /**
     * Return a fresh access token for the given user email.
     * Refreshes using the stored refresh_token and updates expiry if present.
     */
    public String getFreshAccessTokenForEmail(String email) {
        var user = users.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        var acct = accounts.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("not linked"));

        // Always refresh (simple + reliable for now)
        return refreshAndPersist(acct);
    }

    private String refreshAndPersist(SpotifyAccount acct) {
        Map<String, Object> resp = spotifyClient.refreshAccessToken(acct.getRefreshToken());
        if (resp == null || resp.get("access_token") == null) {
            throw new RuntimeException("token_refresh_failed");
        }
        String accessToken = (String) resp.get("access_token");

        return accessToken;
    }

    public String getValidAccessTokenForUser(Long userId) {
        var acct = accounts.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Spotify not linked"));
        // Always refresh for now; we are not tracking expiry on the entity yet
        return refreshAndPersist(acct);
    }
}
