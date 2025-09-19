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
import org.springframework.web.bind.annotation.RequestParam;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



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
    public ResponseEntity<?> playlists(
            Authentication auth,
            @RequestParam(name = "limit", defaultValue = "12") int limit,
            @RequestParam(name = "offset", defaultValue = "0") int offset) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthenticated"));
        }
        if (limit < 1) limit = 1; // guard rails
        if (limit > 50) limit = 50; // Spotify max 50
        if (offset < 0) offset = 0;

        final int finalLimit = limit;
        final int finalOffset = offset;

        String bearer = spotifyService.getFreshAccessTokenForEmail(auth.getName());

        @SuppressWarnings("unchecked")
        Map<String, Object> resp = (Map<String, Object>) apiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/me/playlists")
                        .queryParam("limit", finalLimit)
                        .queryParam("offset", finalOffset)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // Map Spotify response -> DTO
        List<Map<String, Object>> items = new ArrayList<>();
        if (resp != null) {
            Object rawItems = resp.get("items");
            if (rawItems instanceof List<?> list) {
                for (Object o : list) {
                    if (!(o instanceof Map)) continue; // keep guard
                    @SuppressWarnings("unchecked")
                    Map<String, Object> p = (Map<String, Object>) o;
                    String id = Objects.toString(p.get("id"), "");
                    String name = Objects.toString(p.get("name"), "");

                    String owner = "";
                    Object ownerObj = p.get("owner");
                    if (ownerObj instanceof Map<?,?>) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> om = (Map<String, Object>) ownerObj;
                        owner = Objects.toString(om.get("display_name"), null);
                        if (owner == null) owner = Objects.toString(om.get("id"), "");
                    }

                    int tracks = 0;
                    Object tracksObj = p.get("tracks");
                    if (tracksObj instanceof Map<?,?>) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> tm = (Map<String, Object>) tracksObj;
                        Object total = tm.get("total");
                        if (total instanceof Number n) tracks = n.intValue();
                    }

                    String imageUrl = null;
                    Object imagesObj = p.get("images");
                    if (imagesObj instanceof List<?> il && !il.isEmpty()) {
                        Object first = il.get(0);
                        if (first instanceof Map<?,?>) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> im = (Map<String, Object>) first;
                            imageUrl = Objects.toString(im.get("url"), null);
                        }
                    }

                    String externalUrl = null;
                    Object extObj = p.get("external_urls");
                    if (extObj instanceof Map<?,?>) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> em = (Map<String, Object>) extObj;
                        externalUrl = Objects.toString(em.get("spotify"), null);
                    }

                    Map<String, Object> out = new HashMap<>();
                    out.put("id", id);
                    out.put("name", name);
                    out.put("owner", owner);
                    out.put("tracks", tracks);
                    out.put("imageUrl", imageUrl);
                    out.put("externalUrl", externalUrl);
                    items.add(out);
                }
            }
        }

        boolean hasMore = resp != null && resp.get("next") != null;
        Integer nextOffset = hasMore ? offset + items.size() : null;

        Map<String, Object> body = new HashMap<>();
        body.put("items", items);
        body.put("nextOffset", nextOffset); // may be null; that's fine
        body.put("hasMore", hasMore);
        return ResponseEntity.ok(body);
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
