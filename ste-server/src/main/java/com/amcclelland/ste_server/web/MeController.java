package com.amcclelland.ste_server.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class MeController {

    @GetMapping("/api/me")
    public ResponseEntity<Map<String, Object>> me(Authentication auth) {
        if (auth == null)
            return ResponseEntity.status(401).build();
        var roles = auth.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of(
                "username", auth.getName(),
                "roles", roles));
    }
}
