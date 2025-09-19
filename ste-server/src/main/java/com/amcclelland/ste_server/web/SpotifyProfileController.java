package com.amcclelland.ste_server.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import com.amcclelland.ste_server.application.SpotifyService;
import com.amcclelland.ste_server.infra.SpotifyClient;
import com.amcclelland.ste_server.infra.SpotifyAccountRepository;
import com.amcclelland.ste_server.infra.UserRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.Map;



@RestController
@RequestMapping("/api/spotify")
public class SpotifyProfileController {

    private final UserRepository users;
    private final SpotifyAccountRepository accounts;
    private final SpotifyService spotifyService;
    private final SpotifyClient spotifyClient;
    private final WebClient apiClient = WebClient.create("https://api.spotify.com");

    public SpotifyProfileController(SpotifyService spotifyService, SpotifyClient spotifyClient, UserRepository users,
            SpotifyAccountRepository accounts) {
        this.spotifyService = spotifyService;
        this.spotifyClient = spotifyClient;
        this.users = users;
        this.accounts = accounts;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthenticated"));
        }
        String bearer = spotifyService.getFreshAccessTokenForEmail(auth.getName());
        Map<String, Object> me = spotifyClient.getCurrentUserProfile(bearer);
        return ResponseEntity.ok(me);
    }

    @GetMapping("/playlists")
    public ResponseEntity<?> playlists(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthenticated"));
        }
        String bearer = spotifyService.getFreshAccessTokenForEmail(auth.getName());
        Map<String, Object> resp = apiClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/me/playlists").queryParam("limit", 12).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        return ResponseEntity.ok(resp);
    }

    // DELETE spotify link endpoint
    @DeleteMapping("/link")
    public ResponseEntity<?> unlink(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthenticated"));
        }
        var user = users.findByEmail(auth.getName()).orElse(null);
        if (user == null) {
            // Don’t leak info; pretend it worked.
            return ResponseEntity.noContent().build();
        }
        accounts.findByUserId(user.getId()).ifPresent(accounts::delete);
        return ResponseEntity.noContent().build();
    }
}
