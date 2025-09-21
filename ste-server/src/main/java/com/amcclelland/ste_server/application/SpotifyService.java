package com.amcclelland.ste_server.application;

import com.amcclelland.ste_server.domain.SpotifyAccount;
import com.amcclelland.ste_server.infra.SpotifyAccountRepository;
import com.amcclelland.ste_server.infra.SpotifyClient;
import com.amcclelland.ste_server.infra.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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
        var user = users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "USER_NOT_FOUND"));
        var acct = accounts.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(CONFLICT, "SPOTIFY_NOT_LINKED"));

        // Always refresh
        return refreshAndPersist(acct);
    }

    private String refreshAndPersist(SpotifyAccount acct) {
        Map<String, Object> resp = spotifyClient.refreshAccessToken(acct.getRefreshToken());
        if (resp == null || resp.get("access_token") == null) {
            // Token revoked/expired/invalid → treat as not linked or unauthorized
            throw new ResponseStatusException(UNAUTHORIZED, "SPOTIFY_TOKEN_INVALID");
            // or: new ResponseStatusException(CONFLICT, "SPOTIFY_NOT_LINKED");
        }
        return (String) resp.get("access_token");
    }

    public String getValidAccessTokenForUser(Long userId) {

        var acct = accounts.findByUserId(userId).orElseThrow(() -> new IllegalStateException("not_linked"));
        // Always refresh for now;
        return refreshAndPersist(acct);
    }
}
