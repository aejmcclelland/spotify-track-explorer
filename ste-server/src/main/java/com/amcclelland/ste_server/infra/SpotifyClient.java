package com.amcclelland.ste_server.infra;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.amcclelland.ste_server.config.SpotifyProperties;

/**
 * Thin HTTP client for Spotify.
 *
 * Responsibility here is only to call Spotify endpoints. Persisting tokens
 * (refresh/access) belongs in a service/repository layer.
 */
@Component
public class SpotifyClient {

    private final WebClient tokenClient;
    private final WebClient apiClient;
    private final SpotifyProperties props;

    public SpotifyClient(SpotifyProperties props) {
        this.props = props;
        this.tokenClient = WebClient.builder()
                .baseUrl("https://accounts.spotify.com")
                .defaultHeaders(h -> h.setBasicAuth(props.getClientId(), props.getClientSecret()))
                .build();
        this.apiClient = WebClient.builder()
                .baseUrl("https://api.spotify.com")
                .build();
    }

    /**
     * Exchange a refresh_token for a new access_token.
     * @param refreshToken the user's stored refresh_token
     * @return token response as a Map (access_token, token_type, expires_in, scope, etc.)
     */
    public Map<String, Object> refreshAccessToken(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);

        return tokenClient.post()
                .uri("/api/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    /**
     * Call GET /v1/me with a bearer token.
     */
    public Map<String, Object> getCurrentUserProfile(String accessToken) {
        return apiClient.get()
                .uri("/v1/me")
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
