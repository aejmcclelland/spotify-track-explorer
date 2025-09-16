package com.amcclelland.ste_server.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import com.amcclelland.ste_server.web.dto.LoginRequest;
import com.amcclelland.ste_server.web.dto.RegisterRequest;
import com.amcclelland.ste_server.application.UserService;
import com.amcclelland.ste_server.config.AuthManagerConfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> token(@RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));

        var principal = (UserDetails) auth.getPrincipal();
        var now = Instant.now();
        var roleNames = principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();

        var claims = JwtClaimsSet.builder()
                .issuer("ste")
                .issuedAt(now)
                .expiresAt(now.plus(60, ChronoUnit.MINUTES))
                .subject(principal.getUsername())
                .claim("roles", roleNames)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return ResponseEntity.ok(Map.of("access_token", token, "token_type", "Bearer"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        var user = userService.register(req.email(), req.password());
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "role", user.getRole()));
    }
}
