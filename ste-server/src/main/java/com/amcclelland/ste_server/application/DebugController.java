package com.amcclelland.ste_server.application;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.util.Map;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatus;

@RestController
public class DebugController {
    private final SpotifyService spotifyService;

    public DebugController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }
    @GetMapping("/api/debug/whoami")
    public ResponseEntity<Map<String, Object>> whoami(Principal principal,
            @RequestHeader Map<String, String> headers) {
        return ResponseEntity.ok(Map.of(
                "principal", principal == null ? null : principal.getName(),
                "hasAuthorization", headers.containsKey("authorization"),
                "authorizationPrefix", headers.getOrDefault("authorization", "").split(" ")[0],
                "hasCookie", headers.containsKey("cookie")));
    }

    @GetMapping("/api/debug/spotify/probe")
    public ResponseEntity<Map<String, Object>> probe(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "UNAUTHENTICATED"));
            }
            String email = principal.getName();
            String accessToken = spotifyService.getFreshAccessTokenForEmail(email);

            WebClient client = WebClient.builder()
                    .baseUrl("https://api.spotify.com/v1")
                    .defaultHeader("Authorization", "Bearer " + accessToken)
                    .build();

            Map me = client.get().uri("/me")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            var playlistsResp = client.get().uri("/me/playlists?limit=5")
                    .exchangeToMono(res -> res.bodyToMono(String.class)
                            .map(body -> Map.of(
                                    "status", res.statusCode().value(),
                                    "body", body)))
                    .block();

            return ResponseEntity.ok(Map.of(
                    "me", me,
                    "playlists", playlistsResp
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
