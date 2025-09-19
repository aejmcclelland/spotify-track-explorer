package com.amcclelland.ste_server.web;

import com.amcclelland.ste_server.config.SpotifyProperties;
import com.amcclelland.ste_server.infra.SpotifyAccountRepository;
import com.amcclelland.ste_server.infra.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyProfileController {

    private final SpotifyProperties props;
    private final UserRepository users;
    private final SpotifyAccountRepository accounts;
    private final WebClient tokenClient = WebClient.create("https://accounts.spotify.com");
    private final WebClient apiClient = WebClient.create("https://api.spotify.com");

    public SpotifyProfileController(SpotifyProperties props,
            UserRepository users,
            SpotifyAccountRepository accounts) {
        this.props = props;
        this.users = users;
        this.accounts = accounts;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthenticated"));
        }

        var user = users.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("user not found"));

        var acct = accounts.findByUserId(user.getId())
                .orElse(null);
        if (acct == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "not_linked"));
        }

        // 1) Refresh access token
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", acct.getRefreshToken());

        Map<String, Object> tokenResp = tokenClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/token").build())
                .headers(h -> h.setBasicAuth(props.getClientId(), props.getClientSecret()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (tokenResp == null || tokenResp.get("access_token") == null) {
            return ResponseEntity.status(502).body(Map.of("error", "token_refresh_failed", "details", tokenResp));
        }

        String accessToken = (String) tokenResp.get("access_token");

        // 2) Call Spotify /me
        Map<String, Object> me = apiClient.get()
                .uri("/v1/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return ResponseEntity.ok(me);
    }

    @GetMapping("/playlists")
    public ResponseEntity<?> playlists(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthenticated"));
        }

        var user = users.findByEmail(auth.getName()).orElseThrow();
        var acct = accounts.findByUserId(user.getId()).orElse(null);
        if (acct == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "not_linked"));
        }

        // Refresh access token
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", acct.getRefreshToken());

        Map<String, Object> tokenResp = tokenClient.post()
                .uri("/api/token")
                .headers(h -> h.setBasicAuth(props.getClientId(), props.getClientSecret()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (tokenResp == null || tokenResp.get("access_token") == null) {
            return ResponseEntity.status(502).body(Map.of("error", "token_refresh_failed", "details", tokenResp));
        }
        String accessToken = (String) tokenResp.get("access_token");

        // Fetch playlists (limit 12 for a nice grid)
        Map<String, Object> resp = apiClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/me/playlists").queryParam("limit", 12).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // Return the raw Spotify JSON for speed, or map to a minimal DTO:
        return ResponseEntity.ok(resp);
    }
}
