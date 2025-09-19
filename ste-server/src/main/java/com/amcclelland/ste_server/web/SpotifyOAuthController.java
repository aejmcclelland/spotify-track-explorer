package com.amcclelland.ste_server.web;

import com.amcclelland.ste_server.config.SpotifyProperties;
import com.amcclelland.ste_server.domain.SpotifyAccount;
import com.amcclelland.ste_server.infra.SpotifyAccountRepository;
import com.amcclelland.ste_server.infra.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyOAuthController {
    private final SpotifyProperties props;
    private final UserRepository users;
    private final SpotifyAccountRepository accounts;
    private final WebClient tokenClient;
    private final WebClient apiClient;

    public SpotifyOAuthController(SpotifyProperties props,
            UserRepository users,
            SpotifyAccountRepository accounts) {
        this.props = props;
        this.users = users;
        this.accounts = accounts;
        this.tokenClient = WebClient.builder()
                .baseUrl("https://accounts.spotify.com")
                .defaultHeaders(h -> h.setBasicAuth(props.getClientId(), props.getClientSecret()))
                .build();
        this.apiClient = WebClient.builder()
                .baseUrl("https://api.spotify.com")
                .build();
    }

    @GetMapping("/authorize")
    public Map<String, String> authorize() {
        // A simple state token for the client to echo back (optional to validate in
        // MVP)
        String state = UUID.randomUUID().toString();
        String url = "https://accounts.spotify.com/authorize" +
                "?response_type=code" +
                "&client_id=" + enc(props.getClientId()) +
                "&redirect_uri=" + enc(props.getRedirectUri()) +
                "&scope=" + enc(props.getScopes()) +
                "&state=" + enc(state);
        return Map.of("authorize_url", url, "state", state);
    }

    @PostMapping(path = "/exchange", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exchange(@RequestBody ExchangeRequest body,
            org.springframework.security.core.Authentication authentication) {
        if (body == null || body.code() == null || body.code().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "missing_code"));
        }
        final String email = (authentication != null) ? authentication.getName() : null;
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthenticated"));
        }
        var user = users.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found"));
        // 1) Exchange code for tokens
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", body.code());
        form.add("redirect_uri", props.getRedirectUri());
        Map<String, Object> tokenResp = tokenClient.post()
                .uri("/api/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        String accessToken = (String) tokenResp.get("access_token");
        String refreshToken = (String) tokenResp.get("refresh_token");
        Integer expiresIn = toInteger(tokenResp.get("expires_in"));
        // 2) Fetch profile
        Map<String, Object> profile = apiClient.get()
                .uri("/v1/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        String spotifyUserId = (String) profile.get("id");
        String displayName = Optional.ofNullable((String) profile.get("display_name"))
                .orElse(user.getEmail());

        // 3) Upsert account row
        var existing = accounts.findByUserId(user.getId()).orElse(null);
        if (existing == null) {
            accounts.save(new SpotifyAccount(
                    user.getId(), spotifyUserId, displayName, refreshToken, props.getScopes(), expiresIn));
        } else {
            existing.setSpotifyUserId(spotifyUserId);
            existing.setDisplayName(displayName);
            if (refreshToken != null && !refreshToken.isBlank()) {
                existing.setRefreshToken(refreshToken);
            }
            existing.setExpiresInSeconds(expiresIn);
            existing.setScope(props.getScopes());
            accounts.save(existing);
        }
        return ResponseEntity.ok(Map.of(
                "linked", true,
                "spotify_user_id", spotifyUserId,
                "display_name", displayName));
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static Integer toInteger(Object o) {
        if (o == null) return null;
        if (o instanceof Integer i) return i;
        if (o instanceof Number n) return n.intValue();
        return Integer.valueOf(String.valueOf(o));
    }

    public record ExchangeRequest(String code, String state) {
    }

}
