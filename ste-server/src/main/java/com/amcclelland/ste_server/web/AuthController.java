package com.amcclelland.ste_server.web;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import com.amcclelland.ste_server.web.dto.LoginRequest;
import com.amcclelland.ste_server.web.dto.RegisterRequest;
import com.amcclelland.ste_server.application.UserService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.AuthenticationException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody LoginRequest req) {
        log.debug("/api/auth/token called for username={}", (req != null ? req.username() : null));
        if (req == null || req.username() == null || req.username().isBlank() || req.password() == null || req.password().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "username and password are required"));
        }
        try {
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
        } catch (BadCredentialsException | UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication failed"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        log.debug("/api/auth/register called with email={}", (req != null ? req.email() : null));
        if (req == null || req.email() == null || req.email().isBlank() || req.password() == null || req.password().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "email and password are required"));
        }
        try {
            var email = req.email().trim();
            var user = userService.register(email, req.password());
            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "role", user.getRole()));
        } catch (IllegalArgumentException ex) {
            // Thrown by service if email already exists
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "email already registered"));
        } catch (DataIntegrityViolationException ex) {
            // Unique constraint violation from the DB layer
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "email already registered"));
        } catch (Exception ex) {
            log.error("/api/auth/register failed", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "internal_error"));
        }
    }
}
