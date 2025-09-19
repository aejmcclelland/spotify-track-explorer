package com.amcclelland.ste_server.infra;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.amcclelland.ste_server.config.SpotifyProperties;

@Component
public class SpotifyClient {

    private final WebClient tokenClient;

    public SpotifyClient(SpotifyProperties props) {
        this.tokenClient = WebClient.builder()
                .baseUrl("https://accounts.spotify.com")
                .defaultHeaders(h -> h.setBasicAuth(props.getClientId(), props.getClientSecret()))
                .build();
    }

    public WebClient getTokenClient() {
        return tokenClient;
    }
}
