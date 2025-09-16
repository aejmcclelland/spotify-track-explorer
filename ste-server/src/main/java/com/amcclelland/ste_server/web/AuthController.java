package com.amcclelland.ste_server.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import com.amcclelland.ste_server.web.dto.LoginRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtEncoder jwtEncoder;

  public AuthController(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
    this.authenticationManager = authenticationManager;
    this.jwtEncoder = jwtEncoder;
  }

  @PostMapping("/token")
  public ResponseEntity<Map<String, String>> token(@RequestBody LoginRequest req) {
    Authentication auth = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(req.username(), req.password()));

    var principal = (UserDetails) auth.getPrincipal();
    var now = Instant.now();
    var claims = JwtClaimsSet.builder()
        .issuer("ste")
        .issuedAt(now)
        .expiresAt(now.plus(60, ChronoUnit.MINUTES))
        .subject(principal.getUsername())
        .claim("roles", principal.getAuthorities())
        .build();

    String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    return ResponseEntity.ok(Map.of("access_token", token, "token_type", "Bearer"));
  }
}
